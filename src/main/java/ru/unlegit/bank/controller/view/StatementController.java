package ru.unlegit.bank.controller.view;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.unlegit.bank.entity.StatementEntity;
import ru.unlegit.bank.service.StatementService;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("/statement")
public final class StatementController {

    private final StatementService statementService;

    @GetMapping
    public String statements() {
        return "statements";
    }

    @GetMapping("/generate")
    public void generateStatement(
            @RequestParam("type") StatementEntity type,
            @RequestParam("from") String fromStr,
            @RequestParam("to") String toStr,
            HttpServletResponse response
    ) throws Exception {
        Timestamp startDate = Timestamp.valueOf(LocalDate.parse(fromStr).atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(LocalDate.parse(toStr).atStartOfDay());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"Statement for " + type.getName() + ".xlsx\"");

        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                OutputStream responseStream = response.getOutputStream()
        ) {
            statementService.exportStatement(type, startDate, endDate, byteStream);

            response.setContentLength(byteStream.size());

            byteStream.writeTo(responseStream);
        }
    }
}