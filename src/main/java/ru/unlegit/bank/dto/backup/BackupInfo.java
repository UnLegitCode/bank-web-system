package ru.unlegit.bank.dto.backup;

import java.time.Instant;

public record BackupInfo(String name, Instant date, long size) {}