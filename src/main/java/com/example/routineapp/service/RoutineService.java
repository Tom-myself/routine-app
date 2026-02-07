package com.example.routineapp.service;

import com.example.routineapp.entity.Routine;
import com.example.routineapp.entity.User;
import com.example.routineapp.repository.RoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;

    public RoutineService(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Transactional(readOnly = true)
    public List<Routine> findByUser(User user) {
        return routineRepository.findByUserIdOrderByDayOfWeekAscStartTimeAsc(user.getId());
    }

    /**
     * 1週間分のルーティーンを曜日ごとにまとめて返す（月〜日）
     */
    @Transactional(readOnly = true)
    public Map<DayOfWeek, List<Routine>> findWeeklyRoutinesByUser(User user) {
        List<Routine> all = findByUser(user);
        Map<DayOfWeek, List<Routine>> map = all.stream()
                .collect(Collectors.groupingBy(Routine::getDayOfWeek, () -> new TreeMap<>(Comparator.comparingInt(DayOfWeek::getValue)), Collectors.toList()));
        for (DayOfWeek d : DayOfWeek.values()) {
            map.putIfAbsent(d, Collections.emptyList());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Optional<Routine> findByIdAndUser(Long id, User user) {
        return routineRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(user.getId()));
    }

    @Transactional
    public Routine create(Routine routine, User user) {
        routine.setUser(user);
        return routineRepository.save(routine);
    }

    @Transactional
    public Routine update(Routine routine, User user) {
        int updated = routineRepository.updateRoutineForUser(
                routine.getId(),
                user.getId(),
                routine.getTitle(),
                routine.getDescription(),
                routine.getDayOfWeek(),
                routine.getStartTime(),
                routine.getDurationMinutes()
        );
        if (updated == 0) {
            throw new IllegalArgumentException("ルーティーンが見つかりません");
        }
        // 更新後の表示用に再取得（詳細画面へリダイレクトするため）
        return routineRepository.findById(routine.getId())
                .orElseThrow(() -> new IllegalArgumentException("ルーティーンが見つかりません"));
    }

    @Transactional
    public void deleteById(Long id, User user) {
        int deleted = routineRepository.deleteByIdAndUserId(id, user.getId());
        if (deleted == 0) {
            throw new IllegalArgumentException("ルーティーンが見つかりません");
        }
    }
}
