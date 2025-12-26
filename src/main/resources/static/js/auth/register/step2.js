document.addEventListener('DOMContentLoaded', function () {
    const seriesInput = document.querySelector('input[th\\:field="*{passportSeries}"]');
    const numberInput = document.querySelector('input[th\\:field="*{passportNumber}"]');

    if (seriesInput) {
        seriesInput.addEventListener('input', function (e) {
            this.value = this.value.replace(/\D/g, '').slice(0, 4);
        });
    }

    if (numberInput) {
        numberInput.addEventListener('input', function (e) {
            this.value = this.value.replace(/\D/g, '').slice(0, 6);
        });
    }

    const dateInput = document.querySelector('input[th\\:field="*{issueDate}"]');

    if (dateInput) {
        dateInput.max = new Date().toISOString().split('T')[0];
    }
});