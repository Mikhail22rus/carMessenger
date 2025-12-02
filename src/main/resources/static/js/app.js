// ===== ОСНОВНЫЕ ФУНКЦИИ =====

// Инициализация приложения
document.addEventListener('DOMContentLoaded', function() {
    console.log('Car Messenger App loaded');
    initForms();
    initNotifications();
    initCarCards();
});

// ===== РАБОТА С ФОРМАМИ =====
function initForms() {
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
                showNotification('Пожалуйста, заполните все обязательные поля правильно', 'error');
                return false;
            }

            // Показываем loader на кнопке
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                const originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<div class="loader"></div> Обработка...';
                submitBtn.disabled = true;

                // Восстанавливаем кнопку через 5 секунд (на случай ошибки)
                setTimeout(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                }, 5000);
            }

            return true;
        });
    });

    // Добавляем интерактивность полям ввода
    const inputs = document.querySelectorAll('.form-input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });

        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });

        // Реальная валидация
        input.addEventListener('input', function() {
            validateField(this);
        });
    });
}

function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');

    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    return isValid;
}

function validateField(field) {
    const value = field.value.trim();
    const errorElement = field.parentElement.querySelector('.field-error') ||
        createErrorElement(field);

    // Очищаем предыдущую ошибку
    errorElement.textContent = '';
    field.style.borderColor = '';

    if (field.hasAttribute('required') && !value) {
        errorElement.textContent = 'Это поле обязательно для заполнения';
        field.style.borderColor = '#f94144';
        return false;
    }

    // Проверка email
    if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            errorElement.textContent = 'Введите корректный email';
            field.style.borderColor = '#f94144';
            return false;
        }
    }

    // Проверка пароля
    if (field.type === 'password' && value.length < 6) {
        errorElement.textContent = 'Пароль должен быть не менее 6 символов';
        field.style.borderColor = '#f94144';
        return false;
    }

    // Проверка телеграм username
    if (field.name === 'ownerTelegram' && value && !value.startsWith('@')) {
        errorElement.textContent = 'Telegram username должен начинаться с @';
        field.style.borderColor = '#f94144';
        return false;
    }

    return true;
}

function createErrorElement(field) {
    const errorElement = document.createElement('div');
    errorElement.className = 'field-error';
    errorElement.style.color = '#f94144';
    errorElement.style.fontSize = '0.85rem';
    errorElement.style.marginTop = '5px';
    field.parentElement.appendChild(errorElement);
    return errorElement;
}

// ===== УВЕДОМЛЕНИЯ =====
function initNotifications() {
    // Автоматически скрываем уведомления через 5 секунд
    const notifications = document.querySelectorAll('.notification');
    notifications.forEach(notification => {
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateY(-10px)';
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    });
}

function showNotification(message, type = 'info') {
    const container = document.querySelector('.notifications-container') ||
        createNotificationsContainer();

    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${getNotificationIcon(type)}"></i>
        <span>${message}</span>
        <button class="close-notification" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;

    // Стили для кнопки закрытия
    notification.querySelector('.close-notification').style.cssText = `
        margin-left: auto;
        background: none;
        border: none;
        color: inherit;
        cursor: pointer;
        opacity: 0.7;
        transition: opacity 0.3s;
    `;

    container.appendChild(notification);

    // Автоудаление через 5 секунд
    setTimeout(() => {
        if (notification.parentElement) {
            notification.style.opacity = '0';
            notification.style.transform = 'translateY(-10px)';
            setTimeout(() => notification.remove(), 300);
        }
    }, 5000);
}

function getNotificationIcon(type) {
    const icons = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

function createNotificationsContainer() {
    const container = document.createElement('div');
    container.className = 'notifications-container';
    container.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        max-width: 400px;
    `;
    document.body.appendChild(container);
    return container;
}

// ===== РАБОТА С КАРТОЧКАМИ АВТОМОБИЛЕЙ =====
function initCarCards() {
    const cards = document.querySelectorAll('.car-card');

    cards.forEach(card => {
        // Анимация при наведении
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });

        // Кнопка удаления с подтверждением
        const deleteBtn = card.querySelector('.delete-btn');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', function(e) {
                if (!confirm('Вы уверены, что хотите удалить этот автомобиль?')) {
                    e.preventDefault();
                    return false;
                }

                // Показываем лоадер
                const originalText = this.innerHTML;
                this.innerHTML = '<div class="loader"></div>';
                this.disabled = true;

                // Форма будет отправлена автоматически
                return true;
            });
        }
    });
}

// ===== ПОИСК И ФИЛЬТРАЦИЯ =====
function initSearch() {
    const searchInput = document.querySelector('.search-input');
    if (!searchInput) return;

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        const cards = document.querySelectorAll('.car-card');

        cards.forEach(card => {
            const cardText = card.textContent.toLowerCase();
            if (cardText.includes(searchTerm)) {
                card.style.display = 'block';
                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, 10);
            } else {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    card.style.display = 'none';
                }, 300);
            }
        });
    });
}

// ===== МОДАЛЬНЫЕ ОКНА =====
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
        setTimeout(() => {
            modal.style.opacity = '1';
            modal.querySelector('.modal-content').style.transform = 'translateY(0)';
        }, 10);

        // Блокируем скролл страницы
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.opacity = '0';
        modal.querySelector('.modal-content').style.transform = 'translateY(-20px)';

        setTimeout(() => {
            modal.style.display = 'none';
            document.body.style.overflow = '';
        }, 300);
    }
}

// ===== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ =====
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showNotification('Скопировано в буфер обмена', 'success');
    }).catch(err => {
        showNotification('Ошибка копирования', 'error');
    });
}

// ===== API ВЗАИМОДЕЙСТВИЕ =====
async function checkTelegramUser(username) {
    if (!username.startsWith('@')) return false;

    try {
        // Здесь можно добавить реальную проверку Telegram username
        // Пока просто имитируем задержку
        await new Promise(resolve => setTimeout(resolve, 1000));

        // Для демо - считаем валидным любой username с @
        return username.length > 1;
    } catch (error) {
        console.error('Error checking Telegram user:', error);
        return false;
    }
}

// Экспортируем функции для использования в HTML
window.showNotification = showNotification;
window.openModal = openModal;
window.closeModal = closeModal;
window.copyToClipboard = copyToClipboard;
window.formatDate = formatDate;