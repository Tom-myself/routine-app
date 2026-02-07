package com.example.routineapp.service;

import com.example.routineapp.entity.Routine;
import com.example.routineapp.entity.User;
import com.example.routineapp.repository.RoutineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private RoutineService routineService;

    private static User createUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setEmail("test@example.com");
        u.setName("Test User");
        return u;
    }

    private static Routine createRoutine(Long id, User user) {
        Routine r = new Routine();
        r.setId(id);
        r.setTitle("英語の勉強");
        r.setDayOfWeek(DayOfWeek.MONDAY);
        r.setStartTime(LocalTime.of(9, 0));
        r.setDurationMinutes(30);
        r.setUser(user);
        return r;
    }

    @Nested
    @DisplayName("findByUser")
    class FindByUser {
        @Test
        void ユーザーのルーティーン一覧が返る() {
            User user = createUser(1L);
            Routine r1 = createRoutine(1L, user);
            when(routineRepository.findByUserIdOrderByDayOfWeekAscStartTimeAsc(1L))
                    .thenReturn(List.of(r1));

            List<Routine> result = routineService.findByUser(user);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("英語の勉強");
        }
    }

    @Nested
    @DisplayName("findByIdAndUser")
    class FindByIdAndUser {
        @Test
        void 存在し所有者が一致すれば返す() {
            User user = createUser(1L);
            Routine r = createRoutine(1L, user);
            when(routineRepository.findById(1L)).thenReturn(Optional.of(r));

            Optional<Routine> result = routineService.findByIdAndUser(1L, user);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }

        @Test
        void 他ユーザー所有ならempty() {
            User owner = createUser(1L);
            User other = createUser(2L);
            Routine r = createRoutine(1L, owner);
            when(routineRepository.findById(1L)).thenReturn(Optional.of(r));

            Optional<Routine> result = routineService.findByIdAndUser(1L, other);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        void ルーティーンが保存される() {
            User user = createUser(1L);
            Routine input = createRoutine(null, null);
            Routine saved = createRoutine(10L, user);
            when(routineRepository.save(any(Routine.class))).thenReturn(saved);

            Routine result = routineService.create(input, user);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(input.getUser()).isEqualTo(user);
            verify(routineRepository).save(input);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        void JPQLで更新され更新後のエンティティが返る() {
            User user = createUser(1L);
            Routine input = createRoutine(1L, user);
            input.setTitle("更新後タイトル");
            Routine after = createRoutine(1L, user);
            after.setTitle("更新後タイトル");
            when(routineRepository.updateRoutineForUser(eq(1L), eq(1L), any(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(routineRepository.findById(1L)).thenReturn(Optional.of(after));

            Routine result = routineService.update(input, user);

            assertThat(result.getTitle()).isEqualTo("更新後タイトル");
            verify(routineRepository).updateRoutineForUser(1L, 1L, "更新後タイトル", input.getDescription(),
                    DayOfWeek.MONDAY, LocalTime.of(9, 0), 30);
        }

        @Test
        void 対象がなければ例外() {
            User user = createUser(1L);
            Routine input = createRoutine(999L, user);
            when(routineRepository.updateRoutineForUser(eq(999L), eq(1L), any(), any(), any(), any(), any()))
                    .thenReturn(0);

            assertThatThrownBy(() -> routineService.update(input, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ルーティーンが見つかりません");
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        void JPQLで削除される() {
            User user = createUser(1L);
            when(routineRepository.deleteByIdAndUserId(1L, 1L)).thenReturn(1);

            routineService.deleteById(1L, user);

            verify(routineRepository).deleteByIdAndUserId(1L, 1L);
        }

        @Test
        void 対象がなければ例外() {
            User user = createUser(1L);
            when(routineRepository.deleteByIdAndUserId(1L, 1L)).thenReturn(0);

            assertThatThrownBy(() -> routineService.deleteById(1L, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ルーティーンが見つかりません");
        }
    }
}
