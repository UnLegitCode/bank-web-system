package ru.unlegit.bank.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StatementEntity {

    CARD("bank cards", new String[]{"ID", "Номер карты", "Заблокирована", "Закрыта", "Открыт"}) {
        @Override
        public void writeData(BaseEntity entity, Row row) {
            Card card = (Card) entity;

            row.createCell(0).setCellValue(card.getId());
            row.createCell(1).setCellValue(card.getNumber());
            row.createCell(2).setCellValue(card.isBlocked());
            row.createCell(3).setCellValue(card.isClosed());
            row.createCell(4).setCellValue(card.formatOpenedAt());
        }
    },
    DEPOSIT("deposits", new String[]{"ID", "Авто-продление", "Закрыт", "Пополняемый", "Частичное снятие", "Сумма", "Открыт"}) {
        @Override
        public void writeData(BaseEntity entity, Row row) {
            Deposit deposit = (Deposit) entity;

            row.createCell(0).setCellValue(deposit.getId());
            row.createCell(1).setCellValue(deposit.isAutoRenew());
            row.createCell(2).setCellValue(deposit.isClosed());
            row.createCell(3).setCellValue(deposit.getTerms().isReplenishable());
            row.createCell(4).setCellValue(deposit.getTerms().isPartialWithdrawal());
            row.createCell(5).setCellValue(deposit.getInitialSum());
            row.createCell(6).setCellValue(deposit.formatOpenedAt());
        }
    },
    CREDIT("credits", new String[]{"ID", "Закрыт", "Процентная ставка", "Сумма", "Открыт"}) {
        @Override
        public void writeData(BaseEntity entity, Row row) {
            Credit credit = (Credit) entity;

            row.createCell(0).setCellValue(credit.getId());
            row.createCell(1).setCellValue(credit.isClosed());
            row.createCell(2).setCellValue(credit.getInterestRate());
            row.createCell(3).setCellValue(credit.getInitialSum());
            row.createCell(4).setCellValue(credit.formatOpenedAt());
        }
    };

    String name;
    String[] header;

    public abstract void writeData(BaseEntity entity, Row row);
}