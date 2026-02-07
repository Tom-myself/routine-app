package com.example.routineapp.repository;

import com.example.routineapp.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByUserIdOrderByDayOfWeekAscStartTimeAsc(Long userId);

    /**
     * Routine更新をJPQLで実行（ユーザー所有チェック込み）
     *
     * @return 更新件数（0なら対象なし or 権限なし）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Routine r
               set r.title = :title,
                   r.description = :description,
                   r.dayOfWeek = :dayOfWeek,
                   r.startTime = :startTime,
                   r.durationMinutes = :durationMinutes
             where r.id = :id
               and r.user.id = :userId
            """)
    int updateRoutineForUser(@Param("id") Long id,
                             @Param("userId") Long userId,
                             @Param("title") String title,
                             @Param("description") String description,
                             @Param("dayOfWeek") DayOfWeek dayOfWeek,
                             @Param("startTime") LocalTime startTime,
                             @Param("durationMinutes") Integer durationMinutes);

    /**
     * Routine削除をJPQLで実行（ユーザー所有チェック込み）
     *
     * @return 削除件数（0なら対象なし or 権限なし）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from Routine r
             where r.id = :id
               and r.user.id = :userId
            """)
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
