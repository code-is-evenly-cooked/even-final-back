package com.even.zaro.service;

import com.even.zaro.entity.Post;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostRankBaselineMemoryStore {

    private final Map<Long, Integer> baselineRankIndexMap = new ConcurrentHashMap<>();
    private final Map<Long, Integer> prevRankIndexMap = new ConcurrentHashMap<>();


    public Map<Long, Integer> getBaselineRankIndexMap() {
        return baselineRankIndexMap;
    }

    public Map<Long, Integer> getPrevRankIndexMap() {
        return prevRankIndexMap;
    }

    // 기준순위 업데이트 -> 최초 요청시 호출
    public void updateBaselineRank(List<Post> posts) {
        AtomicInteger baselineIndex = new AtomicInteger(1);
        baselineRankIndexMap.clear();
        posts.forEach(post -> baselineRankIndexMap.put(post.getId(), baselineIndex.getAndIncrement()));
    }

    // 직전 순위 업데이트 -> 매 요청시 마지막에 호출 (rankChange 계산에 활용)
    public void updatePrevRank(List<Post> posts) {
        AtomicInteger currentRankIndex = new AtomicInteger(1);
        prevRankIndexMap.clear();
        posts.forEach(post -> prevRankIndexMap.put(post.getId(), currentRankIndex.getAndIncrement()));
    }
}