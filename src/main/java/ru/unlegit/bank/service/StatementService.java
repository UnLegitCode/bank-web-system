package ru.unlegit.bank.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.BaseEntity;
import ru.unlegit.bank.entity.StatementEntity;
import ru.unlegit.bank.repository.CardRepository;
import ru.unlegit.bank.repository.CreditRepository;
import ru.unlegit.bank.repository.DepositRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class StatementService {

    CardRepository cardRepository;
    DepositRepository depositRepository;
    CreditRepository creditRepository;

    public void exportStatement(
            StatementEntity entity, Timestamp startDate, Timestamp endDate, OutputStream destination
    ) throws IOException {
        List<? extends BaseEntity> entities;

        switch (entity) {
            case CARD -> entities = cardRepository.findAllByOpenedAtBetween(startDate, endDate);
            case DEPOSIT -> entities = depositRepository.findAllByOpenedAtBetween(startDate, endDate);
            case CREDIT -> entities = creditRepository.findAllByOpenedAtBetween(startDate, endDate);
            default -> throw new IllegalArgumentException("Unknown statement");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Statement for " + entity.getName());
        Row headerRow = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        writeHeader(headerRow, style, entity);

        int rowNum = 1;
        for (BaseEntity entry : entities) {
            entity.writeData(entry, sheet.createRow(rowNum++));
        }

        workbook.write(destination);
        workbook.close();
    }

    private void writeHeader(Row row, CellStyle style, StatementEntity entity) {
        for (int i = 0; i < entity.getHeader().length; i++) {
            Cell cell = row.createCell(i);

            cell.setCellValue(entity.getHeader()[i]);
            cell.setCellStyle(style);
        }
    }
}