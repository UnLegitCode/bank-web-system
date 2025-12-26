(function () {
    'use strict';
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            const newPin = form.querySelector('[name="newPin"]')?.value;
            const confirmPin = form.querySelector('[name="newPinConfirm"]')?.value;
            if (newPin && confirmPin && newPin !== confirmPin) {
                event.preventDefault();
                event.stopPropagation();
                form.querySelector('[name="newPinConfirm"]').setCustomValidity('ПИН-коды не совпадают');
            } else if (newPin && confirmPin) {
                form.querySelector('[name="newPinConfirm"]').setCustomValidity('');
            }
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
})();