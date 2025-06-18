package com.even.zaro.unit.service;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.post.*;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.event.event.PostDeletedEvent;
import com.even.zaro.global.event.event.PostSavedEvent;
import com.even.zaro.mapper.PostMapper;
import com.even.zaro.repository.FollowRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.PostRankBaselineMemoryStore;
import com.even.zaro.service.PostService;
import com.even.zaro.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private PostMapper postMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PostRankBaselineMemoryStore postRankBaselineMemoryStore;
    @Mock private FollowRepository followRepository;

    private final Long userId = 1L;
    private final Long postId = 10L;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .nickname("이브니")
                .status(Status.ACTIVE)
                .build();
    }

    @Nested
    class CreatePostTest {
        @Test
        void create_success() {
            PostCreateRequest request = new PostCreateRequest("제목", "내용", "DAILY_LIFE", "TIPS", List.of("a.jpg", "b.jpg"), "a.jpg");
            Post post = Post.builder().id(postId).user(user).build();
            PostDetailResponse response = PostDetailResponse.builder().postId(postId).build();

            when(userService.findUserById(userId)).thenReturn(user);
            when(postRepository.save(any())).thenReturn(post);
            when(postMapper.toPostDetailDto(post)).thenReturn(response);

            PostDetailResponse result = postService.createPost(request, userId);

            assertEquals(postId, result.getPostId());
            verify(eventPublisher).publishEvent(any(PostSavedEvent.class));
        }
    }

    @Nested
    class UpdatePostTest {
        @Test
        void update_success() {
            Post post = Post.builder()
                    .id(postId)
                    .user(user)
                    .title("이전 제목")
                    .content("이전 내용")
                    .thumbnailImage("a.jpg")
                    .postImageList(List.of("a.jpg"))
                    .category(Post.Category.DAILY_LIFE)
                    .isDeleted(false)
                    .build();

            PostUpdateRequest request = new PostUpdateRequest("수정제목", "수정내용", "TIPS", List.of("new.jpg"), "new.jpg");

            when(postRepository.findByIdAndIsDeletedFalseAndIsReportedFalse(postId)).thenReturn(Optional.of(post));
            when(userService.findUserById(userId)).thenReturn(user);

            postService.updatePost(postId, request, userId);

            assertEquals("수정제목", post.getTitle());
            assertEquals("수정내용", post.getContent());
            assertEquals("new.jpg", post.getThumbnailImage());
            verify(eventPublisher).publishEvent(any(PostSavedEvent.class));
        }
    }

    @Nested
    class DeletePostTest {
        @Test
        void delete_success() {
            Post post = Post.builder()
                    .id(postId)
                    .user(user)
                    .isDeleted(false)
                    .build();

            when(postRepository.findByIdAndIsDeletedFalseAndIsReportedFalse(postId)).thenReturn(Optional.of(post));
            when(userService.findUserById(userId)).thenReturn(user);

            postService.deletePost(postId, userId);

            assertTrue(post.isDeleted());
            verify(eventPublisher).publishEvent(any(PostDeletedEvent.class));
        }
    }


    @Nested
    class GetHomePostPreviewTest {

        @Test
        void getHomePostPreview_success() {
            List<Post> togetherPosts = List.of(Post.builder().id(1L).build());
            List<Post> dailyLifePosts = List.of(Post.builder().id(2L).build());
            List<Post> randomBuyPosts = List.of(Post.builder().id(3L).build());

            when(postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.TOGETHER)).thenReturn(togetherPosts);
            when(postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.DAILY_LIFE)).thenReturn(dailyLifePosts);
            when(postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.RANDOM_BUY)).thenReturn(randomBuyPosts);

            when(postMapper.toSimplePostDto(any(Post.class))).thenReturn(
                    HomePostPreviewResponse.SimplePostDto.builder().postId(1l).title("같이씁시다").build());
            when(postMapper.toRandomBuyDto(any(Post.class))).thenReturn(
                    HomePostPreviewResponse.RandomBuyPostDto.builder().postId(3l).title("이븐테스트").thumbnailImage("b.jpg").build());

            HomePostPreviewResponse result = postService.getHomePostPreview();

            assertEquals(1, result.getTogether().size());
            assertEquals(1, result.getDailyLife().size());
            assertEquals(1, result.getRandomBuy().size());

            verify(postRepository, times(1)).findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.TOGETHER);
            verify(postRepository, times(1)).findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.DAILY_LIFE);
            verify(postRepository, times(1)).findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.RANDOM_BUY);
        }
    }

    @Nested
    class GetRankedPostsTest {

        @Test
        void getRankedPosts_success() {
            Post post1 = Post.builder().id(1L).title("첫번째").build();
            Post post2 = Post.builder().id(2L).title("두번째").build();
            List<Post> topPosts = List.of(post1, post2);

            when(postRepository.findTopPosts(eq(0),any())).thenReturn(topPosts);
            when(postRankBaselineMemoryStore.getBaselineRankIndexMap()).thenReturn(new ConcurrentHashMap<>());
            when(postRankBaselineMemoryStore.getPrevRankIndexMap()).thenReturn(new ConcurrentHashMap<>());

            List<PostRankResponseDto> result = postService.getRankedPosts();

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getCurrentRankIndex());
            assertEquals(2, result.get(1).getCurrentRankIndex());
            assertEquals(0, result.get(0).getRankChange());
            assertEquals(0, result.get(1).getRankChange());

            verify(postRankBaselineMemoryStore).updateBaselineRank(topPosts);
            verify(postRankBaselineMemoryStore,times(2)).updatePrevRank(topPosts);
        }
    }

    @Nested
    class GetPostDetailsTest {

        @Test
        void getPostDetails_success() {
            Long postId = 1L;
            Long currentUserId = 2L;

            User postOwner = User.builder().id(1L).build();
            User loginUser = User.builder().id(currentUserId).build();

            Post post = Post.builder()
                    .id(postId)
                    .title("제목")
                    .content("내용")
                    .category(Post.Category.TOGETHER)
                    .tag(Post.Tag.GROUP_BUY)
                    .user(postOwner)
                    .build();

            PostDetailResponse mockResponse = PostDetailResponse.builder()
                    .postId(postId)
                    .title("제목")
                    .content("내용")
                    .category(String.valueOf(Post.Category.TOGETHER))
                    .tag(String.valueOf(Post.Tag.GROUP_BUY))
                    .user(PostDetailResponse.UserInfo.builder()
                            .userId(postOwner.getId())
                            .nickname("owner")
                            .profileImage(null)
                            .liveAloneDate(null)
                            .following(false)
                            .build())
                    .build();

            when(postRepository.findByIdAndIsDeletedFalseAndIsReportedFalse(postId)).thenReturn(Optional.of(post));
            when(userService.findUserById(currentUserId)).thenReturn(loginUser);
            when(postMapper.toPostDetailDto(post)).thenReturn(mockResponse);
            when(followRepository.existsByFollowerAndFollowee(loginUser, postOwner)).thenReturn(false);

            PostDetailResponse result = postService.getPostDetail(postId, currentUserId);

            assertEquals(postId, result.getPostId());
            assertEquals("제목", result.getTitle());
            assertEquals("내용", result.getContent());
            assertEquals("TOGETHER", result.getCategory());
            assertEquals("GROUP_BUY", result.getTag());
            assertFalse(result.getUser().isFollowing());
        }
    }

    @Nested
    class getPostListTest{

        @Test
        void getPostList_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Post.Category category = Post.Category.TOGETHER;
            Post post = Post.builder().id(1L).title("테스트").build();
            Page<Post> page = new PageImpl<>(List.of(post));

            when(postRepository.findByCategoryAndIsDeletedFalseAndIsReportedFalse(category, pageable)).thenReturn(page);
            when(postMapper.toPostPreviewDto(any(Post.class))).thenReturn(PostPreviewDto.builder().postId(1L).title("같이쓰기").build());

            PageResponse<PostPreviewDto> result =  postService.getPostListPage("TOGETHER", null, pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("같이쓰기", result.getContent().get(0).getTitle());
        }
    }

}