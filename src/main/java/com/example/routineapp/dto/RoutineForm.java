package com.example.routineapp.dto;

import com.example.routineapp.entity.Routine;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class RoutineForm {

    private Long id;

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "曜日を選択してください")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "開始時刻を入力してください")
    private LocalTime startTime;

    @NotNull(message = "所要時間を入力してください")
    @Min(value = 1, message = "1分以上で入力してください")
    private Integer durationMinutes;

    public static RoutineForm from(Routine routine) {
        RoutineForm form = new RoutineForm();
        form.setId(routine.getId());
        form.setTitle(routine.getTitle());
        form.setDescription(routine.getDescription());
        form.setDayOfWeek(routine.getDayOfWeek());
        form.setStartTime(routine.getStartTime());
        form.setDurationMinutes(routine.getDurationMinutes());
        return form;
    }

    public Routine toEntity() {
        Routine r = new Routine();
        r.setId(id);
        r.setTitle(title);
        r.setDescription(description);
        r.setDayOfWeek(dayOfWeek);
        r.setStartTime(startTime);
        r.setDurationMinutes(durationMinutes);
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
