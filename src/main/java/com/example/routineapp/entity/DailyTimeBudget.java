package com.example.routineapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;

/**
 * 1日に使える時間（分）を曜日ごとに保持するモデル。
 * 「使える時間から逆算して1週間のスケジュールを組む」ための土台テーブル。
 */
@Entity
@Table(
        name = "daily_time_budgets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_daily_time_budgets_user_day", columnNames = {"user_id", "day_of_week"})
        }
)
public class DailyTimeBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Min(0)
    @Column(name = "available_minutes", nullable = false)
    private Integer availableMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getAvailableMinutes() {
        return availableMinutes;
    }

    public void setAvailableMinutes(Integer availableMinutes) {
        this.availableMinutes = availableMinutes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

