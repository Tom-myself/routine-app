package com.example.routineapp.controller;

import com.example.routineapp.dto.RoutineForm;
import com.example.routineapp.entity.Routine;
import com.example.routineapp.service.RoutineService;
import com.example.routineapp.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/routines")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    /**
     * 1週間分のルーティーン一覧（曜日別）
     */
    @GetMapping
    public String list(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        Map<DayOfWeek, List<Routine>> weekly = routineService.findWeeklyRoutinesByUser(principal.getUser());
        model.addAttribute("weeklyRoutines", weekly);
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        return "routines/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        return routineService.findByIdAndUser(id, principal.getUser())
                .map(routine -> {
                    model.addAttribute("routine", routine);
                    return "routines/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "ルーティーンが見つかりません");
                    return "redirect:/routines";
                });
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new RoutineForm());
        model.addAttribute("daysOfWeek", DayOfWeek.values());
        return "routines/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") RoutineForm form,
                         BindingResult result,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("daysOfWeek", DayOfWeek.values());
            return "routines/form";
        }
        Routine routine = routineService.create(form.toEntity(), principal.getUser());
        redirectAttributes.addFlashAttribute("message", "ルーティーンを追加しました");
        return "redirect:/routines/" + routine.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserPrincipal principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        return routineService.findByIdAndUser(id, principal.getUser())
                .map(routine -> {
                    model.addAttribute("form", RoutineForm.from(routine));
                    model.addAttribute("daysOfWeek", DayOfWeek.values());
                    return "routines/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "ルーティーンが見つかりません");
                    return "redirect:/routines";
                });
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") RoutineForm form,
                         BindingResult result,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("daysOfWeek", DayOfWeek.values());
            return "routines/form";
        }
        form.setId(id);
        try {
            routineService.update(form.toEntity(), principal.getUser());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/routines";
        }
        redirectAttributes.addFlashAttribute("message", "ルーティーンを更新しました");
        return "redirect:/routines/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            routineService.deleteById(id, principal.getUser());
            redirectAttributes.addFlashAttribute("message", "ルーティーンを削除しました");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/routines";
    }
}
