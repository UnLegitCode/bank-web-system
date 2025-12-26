(function () {
    'use strict';
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        const amountInput = form.querySelector('[name="amount"]');
        if (amountInput) {
            amountInput.addEventListener('input', () => {
                const min = parseInt(amountInput.min);
                const max = parseInt(amountInput.max);
                const value = parseInt(amountInput.value);
                if (value < min || value > max) {
                    amountInput.setCustomValidity('Некорректная сумма');
                } else {
                    amountInput.setCustomValidity('');
                }
            });
        }

        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
})();