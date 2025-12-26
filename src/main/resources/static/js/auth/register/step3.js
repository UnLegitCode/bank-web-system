document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('togglePassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    togglePassword.addEventListener('click', function() {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });

    toggleConfirmPassword.addEventListener('click', function() {
        const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        confirmPassword.setAttribute('type', type);
        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });

    const termsCheckbox = document.getElementById('termsAgreement');
    const submitButton = document.getElementById('submitButton');

    termsCheckbox.addEventListener('change', function() {
        submitButton.disabled = !this.checked;
    });

    submitButton.disabled = true;
});

function checkPasswordStrength() {
    const password = document.getElementById('password').value;
    const strengthBar = document.getElementById('passwordStrength');

    document.querySelectorAll('.requirement').forEach(req => {
        req.classList.remove('valid');
        req.classList.add('invalid');
        req.querySelector('i').className = 'fas fa-circle';
    });

    let strength = 0;

    if (password.length >= 8) {
        strength++;
        document.getElementById('reqLength').classList.add('valid');
        document.getElementById('reqLength').classList.remove('invalid');
        document.getElementById('reqLength').querySelector('i').className = 'fas fa-check-circle';
    }

    if (/[a-z]/.test(password)) {
        strength++;
        document.getElementById('reqLowercase').classList.add('valid');
        document.getElementById('reqLowercase').classList.remove('invalid');
        document.getElementById('reqLowercase').querySelector('i').className = 'fas fa-check-circle';
    }

    if (/[A-Z]/.test(password)) {
        strength++;
        document.getElementById('reqUppercase').classList.add('valid');
        document.getElementById('reqUppercase').classList.remove('invalid');
        document.getElementById('reqUppercase').querySelector('i').className = 'fas fa-check-circle';
    }

    if (/[0-9]/.test(password)) {
        strength++;
        document.getElementById('reqNumber').classList.add('valid');
        document.getElementById('reqNumber').classList.remove('invalid');
        document.getElementById('reqNumber').querySelector('i').className = 'fas fa-check-circle';
    }

    if (/[!@#$%^&*]/.test(password)) {
        strength++;
        document.getElementById('reqSpecial').classList.add('valid');
        document.getElementById('reqSpecial').classList.remove('invalid');
        document.getElementById('reqSpecial').querySelector('i').className = 'fas fa-check-circle';
    }

    strengthBar.className = 'password-strength ';

    if (strength === 0) {
        strengthBar.className += 'strength-weak';
    } else if (strength <= 3) {
        strengthBar.className += 'strength-medium';
    } else {
        strengthBar.className += 'strength-strong';
    }
}

function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorElement = document.getElementById('passwordMatchError');

    if (password !== confirmPassword && confirmPassword !== '') {
        errorElement.style.display = 'block';
    } else {
        errorElement.style.display = 'none';
    }
}

document.getElementById('registrationForm').addEventListener('submit', function(e) {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const terms = document.getElementById('termsAgreement').checked;

    if (password !== confirmPassword) {
        e.preventDefault();
        document.getElementById('passwordMatchError').style.display = 'block';
        document.getElementById('confirmPassword').focus();
    }

    if (!terms) {
        e.preventDefault();
        alert('Пожалуйста, согласитесь с условиями использования');
    }
});