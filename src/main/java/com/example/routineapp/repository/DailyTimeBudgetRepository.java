package com.example.routineapp.repository;

import com.example.routineapp.entity.DailyTimeBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DailyTimeBudgetRepository extends JpaRepository<DailyTimeBudget, Long> {

    List<DailyTimeBudget> findByUserIdOrderByDayOfWeekAsc(Long userId);

    Optional<DailyTimeBudget> findByUserIdAndDayOfWeek(Long userId, DayOfWeek dayOfWeek);

    /**
     * 使える時間（分）をJPQLで更新
     *
     * @return 更新件数（0なら対象なし）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update DailyTimeBudget b
               set b.availableMinutes = :availableMinutes
             where b.user.id = :userId
               and b.dayOfWeek = :dayOfWeek
            """)
    int updateAvailableMinutes(@Param("userId") Long userId,
                               @Param("dayOfWeek") DayOfWeek dayOfWeek,
                               @Param("availableMinutes") Integer availableMinutes);
}

