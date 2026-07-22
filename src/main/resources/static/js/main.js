/* =============================================
   JOBPORTAL - Premium SaaS UI
   Main JavaScript
   ============================================= */

document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ----- Initialize Everything -----
    initNavbarScroll();
    initThemeToggle();
    initFadeIn();
    initCountUp();
    initMouseGlow();
    initFaqAccordion();
    initPasswordStrength();
    initSidebarToggle();
    initFlashMessages();
    initTooltips();
    initSmoothScroll();
    initMobileSidebar();

    // ----- Navbar Scroll Effect -----
    function initNavbarScroll() {
        const navbar = document.querySelector('.navbar-glass');
        if (!navbar) return;

        const handleScroll = () => {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        };

        window.addEventListener('scroll', handleScroll, { passive: true });
        // Initial check
        handleScroll();
    }

    // ----- Theme Toggle (Light/Dark) -----
    function initThemeToggle() {
        const toggleBtn = document.getElementById('themeToggle');
        const themeIcon = document.getElementById('themeIcon');
        if (!toggleBtn) return;

        // Check saved theme
        const savedTheme = localStorage.getItem('theme') || 'dark';
        document.documentElement.setAttribute('data-theme', savedTheme);
        updateThemeIcon(savedTheme);

        toggleBtn.addEventListener('click', function() {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';

            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);

            // Smooth transition
            document.body.style.transition = 'background 0.3s ease, color 0.3s ease';
        });

        function updateThemeIcon(theme) {
            if (themeIcon) {
                themeIcon.className = theme === 'dark' ? 'fas fa-moon' : 'fas fa-sun';
            }
        }
    }

    // ----- Fade In On Scroll (Intersection Observer) -----
    function initFadeIn() {
        const fadeElements = document.querySelectorAll('.fade-in');
        if (fadeElements.length === 0) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });

        fadeElements.forEach(el => observer.observe(el));
    }

    // ----- Animated Number Counter -----
    function initCountUp() {
        const counters = document.querySelectorAll('.count-up');
        if (counters.length === 0) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const target = entry.target;
                    const targetValue = parseInt(target.getAttribute('data-target')) || 0;
                    const duration = 2000;
                    const startTime = performance.now();

                    function animateNumber(currentTime) {
                        const elapsed = currentTime - startTime;
                        const progress = Math.min(elapsed / duration, 1);

                        // Ease out cubic
                        const eased = 1 - Math.pow(1 - progress, 3);
                        const currentValue = Math.floor(eased * targetValue);

                        target.textContent = currentValue.toLocaleString();

                        if (progress < 1) {
                            requestAnimationFrame(animateNumber);
                        } else {
                            target.textContent = targetValue.toLocaleString();
                        }
                    }

                    requestAnimationFrame(animateNumber);
                    observer.unobserve(target);
                }
            });
        }, { threshold: 0.5 });

        counters.forEach(c => observer.observe(c));
    }

    // ----- Mouse Glow Effect -----
    function initMouseGlow() {
        const glow = document.querySelector('.mouse-glow');
        if (!glow) return;

        document.addEventListener('mousemove', function(e) {
            requestAnimationFrame(() => {
                glow.style.left = e.clientX + 'px';
                glow.style.top = e.clientY + 'px';
            });
        });
    }

    // ----- FAQ Accordion -----
    function initFaqAccordion() {
        const questions = document.querySelectorAll('.faq-question');
        questions.forEach(q => {
            q.addEventListener('click', function() {
                const answer = this.nextElementSibling;
                const isOpen = answer.classList.contains('open');

                // Close all
                document.querySelectorAll('.faq-answer').forEach(a => a.classList.remove('open'));
                document.querySelectorAll('.faq-question').forEach(q => q.classList.remove('active'));

                if (!isOpen) {
                    answer.classList.add('open');
                    this.classList.add('active');
                }
            });
        });
    }

    // ----- Password Strength Meter -----
    function initPasswordStrength() {
        const passwordInput = document.getElementById('passwordInput');
        const strengthBar = document.getElementById('strengthBar');
        const strengthText = document.getElementById('strengthText');

        if (!passwordInput) return;

        passwordInput.addEventListener('input', function() {
            const value = this.value;
            let strength = 0;
            let text = '';
            let color = '';

            if (value.length >= 6) strength += 25;
            if (value.length >= 10) strength += 15;
            if (value.match(/[a-z]/) && value.match(/[A-Z]/)) strength += 20;
            if (value.match(/\d/)) strength += 20;
            if (value.match(/[^a-zA-Z\d]/)) strength += 20;

            if (strength <= 25) {
                text = 'Weak';
                color = '#ef4444';
            } else if (strength <= 50) {
                text = 'Fair';
                color = '#f59e0b';
            } else if (strength <= 75) {
                text = 'Good';
                color = '#3b82f6';
            } else {
                text = 'Strong';
                color = '#10b981';
            }

            if (strengthBar) {
                strengthBar.style.width = Math.min(strength, 100) + '%';
                strengthBar.style.background = color;
            }

            if (strengthText) {
                strengthText.textContent = value.length > 0 ? 'Password strength: ' + text : '';
                strengthText.style.color = color;
            }
        });
    }

    // ----- Dashboard Sidebar Toggle -----
    function initSidebarToggle() {
        const toggleBtn = document.getElementById('sidebarToggle');
        const sidebar = document.querySelector('.dashboard-sidebar');
        const overlay = document.querySelector('.sidebar-overlay');

        if (!toggleBtn || !sidebar) return;

        toggleBtn.addEventListener('click', function() {
            sidebar.classList.toggle('show');
            if (overlay) overlay.classList.toggle('show');
        });

        if (overlay) {
            overlay.addEventListener('click', function() {
                sidebar.classList.remove('show');
                overlay.classList.remove('show');
            });
        }
    }

    // ----- Flash Messages Auto-Hide -----
    function initFlashMessages() {
        const alerts = document.querySelectorAll('.alert-glass');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-10px)';
                setTimeout(() => alert.remove(), 300);
            }, 5000);
        });
    }

    // ----- Bootstrap Tooltips -----
    function initTooltips() {
        const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        if (tooltips.length > 0) {
            tooltips.forEach(el => new bootstrap.Tooltip(el));
        }
    }

    // ----- Smooth Scroll for Anchor Links -----
    function initSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            });
        });
    }

    // ----- Mobile Sidebar (Dashboard) -----
    function initMobileSidebar() {
        // Create overlay if not exists
        if (!document.querySelector('.sidebar-overlay')) {
            const overlay = document.createElement('div');
            overlay.className = 'sidebar-overlay';
            document.body.appendChild(overlay);
        }
    }

    // ----- Search Form Auto-Submit on Enter (Optional) -----
    const searchInputs = document.querySelectorAll('.search-bar input');
    searchInputs.forEach(input => {
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                this.closest('form')?.submit();
            }
        });
    });

    // ----- Table Row Click -----
    document.querySelectorAll('.table-row-click').forEach(row => {
        row.addEventListener('click', function() {
            const url = this.getAttribute('data-href');
            if (url) window.location.href = url;
        });
    });

}); // End DOMContentLoaded

