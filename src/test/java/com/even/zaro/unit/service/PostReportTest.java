package com.even.zaro.unit.service;

import com.even.zaro.dto.post.ReportRequestDTO;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.PostReport;
import com.even.zaro.entity.User;
import com.even.zaro.repository.PostReportRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.PostReportService;
import com.even.zaro.service.PostService;
import com.even.zaro.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.even.zaro.entity.ReportReasonType.ETC;

@ExtendWith(MockitoExtension.class)
class PostReportTest {

    @InjectMocks private PostReportService postReportService;

    @Mock private PostRepository postRepository;
    @Mock private PostReportRepository postReportRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostService postService;
    @Mock private UserService userService;

    @Nested
    class GetPostReportTest {

        @Test
        void getPostReport() {
            Long postId = 1L;
            Long userId = 2L;

            Post post = Post.builder().id(postId).user(User.builder().id(3L).build()).build();
            User reporter = User.builder().id(userId).build();

            ReportRequestDTO request = new ReportRequestDTO(ETC,"비속어가 너무 심합니다");

            when(postService.findPostOrThrow(postId)).thenReturn(post);
            when(userService.findUserById(userId)).thenReturn(reporter);

            postReportService.reportPost(postId, request, userId);

            verify(postReportRepository).save(any(PostReport.class));
        }
    }

}
