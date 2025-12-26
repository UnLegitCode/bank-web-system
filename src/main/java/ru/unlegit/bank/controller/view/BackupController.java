package ru.unlegit.bank.controller.view;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unlegit.bank.service.BackupService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@AllArgsConstructor
@RequestMapping("/backup")
@PreAuthorize("hasRole('ADMIN')")
public class BackupController {

    private final BackupService backupService;

    @GetMapping
    public String listBackups(Model model) {
        model.addAttribute("backups", backupService.listBackups());

        return "backups/list";
    }

    @PostMapping
    public String createBackup(RedirectAttributes redirectAttributes) {
        try {
            backupService.saveBackup();

            redirectAttributes.addFlashAttribute("success", "Бекап успешно создан!");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "error", "Ошибка при создании бекапа: " + exception.getMessage()
            );
        }

        return "redirect:/backup";
    }

    @PostMapping("/restore")
    public String restore(@RequestParam String key, RedirectAttributes redirectAttrs) {
        try {
            backupService.restoreBackup(key);

            redirectAttrs.addFlashAttribute("success", "Бэкап успешно восстановлен: " + key);
        } catch (Exception exception) {
            try (StringWriter writer = new StringWriter(); PrintWriter out = new  PrintWriter(writer)) {
                exception.printStackTrace(out);

                redirectAttrs.addFlashAttribute("error", "Ошибка восстановления: " + writer);
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }

        return "redirect:/backup";
    }

    @GetMapping("/download")
    public void download(@RequestParam String key, HttpServletResponse response) throws IOException {
        Path file = backupService.downloadBackup(key);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + key + "\"");
        response.setContentLengthLong(Files.size(file));

        try (InputStream in = Files.newInputStream(file); OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        } finally {
            Files.deleteIfExists(file);
        }
    }
}