/**
 * EduSmart Custom JavaScript
 * Interactive features and utilities for the learning platform
 */

// Global EduSmart object
window.EduSmart = window.EduSmart || {};

// Utility functions
EduSmart.utils = {
    /**
     * Show a toast notification
     * @param {string} message - The message to display
     * @param {string} type - The type of notification (success, error, warning, info)
     */
    showToast: function(message, type = 'info') {
        const toastContainer = document.getElementById('toast-container') ||
            this.createToastContainer();

        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"></button>
            </div>
        `;

        toastContainer.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();

        // Remove toast from DOM after it's hidden
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    },

    /**
     * Create toast container if it doesn't exist
     */
    createToastContainer: function() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    },

    /**
     * Show loading spinner
     * @param {HTMLElement} element - The element to show spinner in
     */
    showLoading: function(element) {
        element.innerHTML = `
            <div class="d-flex justify-content-center align-items-center p-3">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        `;
    },

    /**
     * Format time duration
     * @param {number} minutes - Duration in minutes
     * @returns {string} Formatted duration
     */
    formatDuration: function(minutes) {
        if (minutes < 60) {
            return `${minutes} min`;
        }
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`;
    },

    /**
     * Debounce function calls
     * @param {Function} func - Function to debounce
     * @param {number} wait - Wait time in milliseconds
     * @returns {Function} Debounced function
     */
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};

// Course catalog functionality
EduSmart.courseCatalog = {
    /**
     * Initialize course catalog features
     */
    init: function() {
        this.bindSearchEvents();
        this.bindFilterEvents();
        this.initInfiniteScroll();
    },

    /**
     * Bind search input events
     */
    bindSearchEvents: function() {
        const searchInput = document.querySelector('input[name="query"]');
        if (searchInput) {
            searchInput.addEventListener('input', EduSmart.utils.debounce(() => {
                this.performSearch();
            }, 300));
        }
    },

    /**
     * Bind filter events
     */
    bindFilterEvents: function() {
        const filterSelects = document.querySelectorAll('select[name]');
        filterSelects.forEach(select => {
            select.addEventListener('change', () => {
                this.performSearch();
            });
        });
    },

    /**
     * Perform search with current filters
     */
    performSearch: function() {
        const form = document.querySelector('form');
        if (form) {
            // Show loading state
            const courseGrid = document.querySelector('.row .col-lg-4');
            if (courseGrid) {
                EduSmart.utils.showLoading(courseGrid.closest('.row'));
            }

            // Submit form (you might want to use AJAX instead)
            form.submit();
        }
    },

    /**
     * Initialize infinite scroll for course loading
     */
    initInfiniteScroll: function() {
        // Implementation for infinite scroll
        // This would typically use Intersection Observer API
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // Load more courses
                    this.loadMoreCourses();
                }
            });
        });

        const loadMoreTrigger = document.getElementById('load-more-trigger');
        if (loadMoreTrigger) {
            observer.observe(loadMoreTrigger);
        }
    },

    /**
     * Load more courses (for infinite scroll)
     */
    loadMoreCourses: function() {
        // AJAX call to load more courses
        console.log('Loading more courses...');
    }
};

// Dashboard functionality
EduSmart.dashboard = {
    /**
     * Initialize dashboard features
     */
    init: function() {
        this.initProgressAnimations();
        this.initRealTimeUpdates();
        this.bindActionButtons();
    },

    /**
     * Initialize progress bar animations
     */
    initProgressAnimations: function() {
        const progressBars = document.querySelectorAll('.progress-bar');
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.width = entry.target.dataset.width || entry.target.style.width;
                }
            });
        });

        progressBars.forEach(bar => {
            bar.dataset.width = bar.style.width;
            bar.style.width = '0%';
            observer.observe(bar);
        });
    },

    /**
     * Initialize real-time updates
     */
    initRealTimeUpdates: function() {
        // Check for WebSocket support and initialize if available
        if (typeof WebSocket !== 'undefined') {
            this.initWebSocketConnection();
        }

        // Fallback to polling for browsers without WebSocket
        setInterval(() => {
            this.checkForUpdates();
        }, 30000); // Check every 30 seconds
    },

    /**
     * Initialize WebSocket connection for real-time updates
     */
    initWebSocketConnection: function() {
        try {
            const socket = new SockJS('/ws');
            const stompClient = Stomp.over(socket);

            stompClient.connect({}, (frame) => {
                console.log('Connected to WebSocket');

                // Subscribe to notifications
                stompClient.subscribe('/user/queue/notifications', (message) => {
                    const notification = JSON.parse(message.body);
                    this.showNotification(notification);
                });

                // Subscribe to progress updates
                stompClient.subscribe('/user/queue/progress', (message) => {
                    const progress = JSON.parse(message.body);
                    this.updateProgress(progress);
                });
            }, (error) => {
                console.error('WebSocket connection error:', error);
            });
        } catch (error) {
            console.error('WebSocket initialization failed:', error);
        }
    },

    /**
     * Check for updates via polling (fallback)
     */
    checkForUpdates: function() {
        // AJAX call to check for updates
        fetch('/api/notifications/unread-count')
            .then(response => response.json())
            .then(data => {
                if (data.count > 0) {
                    this.updateNotificationBadge(data.count);
                }
            })
            .catch(error => console.error('Error checking for updates:', error));
    },

    /**
     * Show real-time notification
     */
    showNotification: function(notification) {
        EduSmart.utils.showToast(notification.message, notification.type || 'info');
        this.updateNotificationBadge();
    },

    /**
     * Update progress display
     */
    updateProgress: function(progress) {
        const progressElement = document.querySelector(`[data-course-id="${progress.courseId}"] .progress-bar`);
        if (progressElement) {
            progressElement.style.width = `${progress.percentage}%`;
            progressElement.nextElementSibling.textContent = `${progress.percentage}%`;
        }
    },

    /**
     * Update notification badge
     */
    updateNotificationBadge: function(count) {
        const badge = document.querySelector('.notification-badge');
        if (badge) {
            badge.textContent = count || '';
            badge.style.display = count > 0 ? 'inline' : 'none';
        }
    },

    /**
     * Bind action button events
     */
    bindActionButtons: function() {
        // Course enrollment buttons
        document.addEventListener('click', (e) => {
            if (e.target.matches('.enroll-btn')) {
                e.preventDefault();
                this.handleEnrollment(e.target);
            }
        });

        // Assessment submission
        document.addEventListener('submit', (e) => {
            if (e.target.matches('.assessment-form')) {
                e.preventDefault();
                this.handleAssessmentSubmission(e.target);
            }
        });
    },

    /**
     * Handle course enrollment
     */
    handleEnrollment: function(button) {
        const courseId = button.dataset.courseId;
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Enrolling...';

        fetch(`/api/enrollments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ courseId: courseId })
        })
        .then(response => response.json())
        .then(data => {
            EduSmart.utils.showToast('Successfully enrolled in the course!', 'success');
            button.innerHTML = 'Enrolled';
            button.classList.remove('btn-primary');
            button.classList.add('btn-success');
        })
        .catch(error => {
            console.error('Enrollment error:', error);
            EduSmart.utils.showToast('Failed to enroll. Please try again.', 'error');
            button.disabled = false;
            button.innerHTML = 'Enroll Now';
        });
    },

    /**
     * Handle assessment submission
     */
    handleAssessmentSubmission: function(form) {
        const formData = new FormData(form);
        const submitBtn = form.querySelector('button[type="submit"]');

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Submitting...';

        fetch(form.action, {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            EduSmart.utils.showToast('Assessment submitted successfully!', 'success');
            // Update progress or redirect to results
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        })
        .catch(error => {
            console.error('Submission error:', error);
            EduSmart.utils.showToast('Failed to submit assessment. Please try again.', 'error');
            submitBtn.disabled = false;
            submitBtn.innerHTML = 'Submit Assessment';
        });
    }
};

// Assessment/Quiz functionality
EduSmart.assessment = {
    /**
     * Initialize assessment features
     */
    init: function() {
        this.initTimer();
        this.bindQuestionNavigation();
        this.initAutoSave();
    },

    /**
     * Initialize quiz timer
     */
    initTimer: function() {
        const timerElement = document.getElementById('quiz-timer');
        if (!timerElement) return;

        let timeLeft = parseInt(timerElement.dataset.timeLeft) || 3600; // Default 1 hour

        const timer = setInterval(() => {
            timeLeft--;
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;

            timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            if (timeLeft <= 300) { // 5 minutes warning
                timerElement.classList.add('text-danger');
            }

            if (timeLeft <= 0) {
                clearInterval(timer);
                this.submitQuiz(true); // Auto-submit
            }
        }, 1000);
    },

    /**
     * Bind question navigation
     */
    bindQuestionNavigation: function() {
        document.addEventListener('click', (e) => {
            if (e.target.matches('.question-nav-btn')) {
                this.navigateToQuestion(e.target.dataset.questionId);
            }
        });
    },

    /**
     * Navigate to specific question
     */
    navigateToQuestion: function(questionId) {
        const questions = document.querySelectorAll('.question-item');
        questions.forEach(q => q.classList.add('d-none'));

        const targetQuestion = document.getElementById(`question-${questionId}`);
        if (targetQuestion) {
            targetQuestion.classList.remove('d-none');
        }

        // Update navigation buttons
        document.querySelectorAll('.question-nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-question-id="${questionId}"]`).classList.add('active');
    },

    /**
     * Initialize auto-save functionality
     */
    initAutoSave: function() {
        let autoSaveTimeout;

        document.addEventListener('input', (e) => {
            if (e.target.matches('input[type="radio"], input[type="checkbox"], textarea')) {
                clearTimeout(autoSaveTimeout);
                autoSaveTimeout = setTimeout(() => {
                    this.autoSaveProgress();
                }, 2000); // Auto-save after 2 seconds of inactivity
            }
        });
    },

    /**
     * Auto-save assessment progress
     */
    autoSaveProgress: function() {
        const form = document.querySelector('.assessment-form');
        if (!form) return;

        const formData = new FormData(form);

        fetch('/api/assessments/auto-save', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                EduSmart.utils.showToast('Progress saved automatically', 'info');
            }
        })
        .catch(error => console.error('Auto-save error:', error));
    },

    /**
     * Submit quiz
     */
    submitQuiz: function(autoSubmit = false) {
        if (autoSubmit) {
            EduSmart.utils.showToast('Time is up! Submitting your assessment...', 'warning');
        }

        const form = document.querySelector('.assessment-form');
        if (form) {
            form.submit();
        }
    }
};

// File upload functionality
EduSmart.fileUpload = {
    /**
     * Initialize file upload features
     */
    init: function() {
        this.bindFileUploadEvents();
        this.initDragAndDrop();
    },

    /**
     * Bind file upload events
     */
    bindFileUploadEvents: function() {
        document.addEventListener('change', (e) => {
            if (e.target.matches('input[type="file"]')) {
                this.handleFileSelection(e.target);
            }
        });
    },

    /**
     * Handle file selection
     */
    handleFileSelection: function(input) {
        const files = Array.from(input.files);
        const maxSize = 10 * 1024 * 1024; // 10MB
        const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'application/msword',
                            'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];

        files.forEach(file => {
            if (file.size > maxSize) {
                EduSmart.utils.showToast(`File ${file.name} is too large. Maximum size is 10MB.`, 'error');
                return;
            }

            if (!allowedTypes.includes(file.type)) {
                EduSmart.utils.showToast(`File type ${file.type} is not allowed for ${file.name}.`, 'error');
                return;
            }

            this.uploadFile(file);
        });
    },

    /**
     * Upload file with progress tracking
     */
    uploadFile: function(file) {
        const formData = new FormData();
        formData.append('file', file);

        const xhr = new XMLHttpRequest();

        // Progress tracking
        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable) {
                const percentComplete = (e.loaded / e.total) * 100;
                this.updateUploadProgress(file.name, percentComplete);
            }
        });

        xhr.addEventListener('load', () => {
            if (xhr.status === 200) {
                EduSmart.utils.showToast(`File ${file.name} uploaded successfully!`, 'success');
                this.addUploadedFileToList(JSON.parse(xhr.responseText));
            } else {
                EduSmart.utils.showToast(`Failed to upload ${file.name}.`, 'error');
            }
        });

        xhr.addEventListener('error', () => {
            EduSmart.utils.showToast(`Network error while uploading ${file.name}.`, 'error');
        });

        xhr.open('POST', '/api/files/upload');
        xhr.send(formData);
    },

    /**
     * Update upload progress
     */
    updateUploadProgress: function(fileName, progress) {
        let progressBar = document.querySelector(`[data-file="${fileName}"] .progress-bar`);
        if (!progressBar) {
            // Create progress bar if it doesn't exist
            const container = document.querySelector('.upload-progress');
            if (container) {
                container.innerHTML += `
                    <div class="mb-2" data-file="${fileName}">
                        <small>${fileName}</small>
                        <div class="progress" style="height: 6px;">
                            <div class="progress-bar" style="width: 0%"></div>
                        </div>
                    </div>
                `;
                progressBar = document.querySelector(`[data-file="${fileName}"] .progress-bar`);
            }
        }

        if (progressBar) {
            progressBar.style.width = `${progress}%`;
        }
    },

    /**
     * Add uploaded file to list
     */
    addUploadedFileToList: function(fileData) {
        const fileList = document.querySelector('.uploaded-files');
        if (fileList) {
            fileList.innerHTML += `
                <div class="file-item d-flex align-items-center justify-content-between p-2 border rounded mb-2">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-file-earmark me-2"></i>
                        <span>${fileData.originalName}</span>
                        <small class="text-muted ms-2">(${this.formatFileSize(fileData.size)})</small>
                    </div>
                    <div>
                        <a href="/api/files/download/${fileData.id}" class="btn btn-sm btn-outline-primary me-1">
                            <i class="bi bi-download"></i>
                        </a>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteFile(${fileData.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            `;
        }
    },

    /**
     * Format file size
     */
    formatFileSize: function(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },

    /**
     * Initialize drag and drop functionality
     */
    initDragAndDrop: function() {
        const dropZones = document.querySelectorAll('.file-drop-zone');

        dropZones.forEach(zone => {
            zone.addEventListener('dragover', (e) => {
                e.preventDefault();
                zone.classList.add('drag-over');
            });

            zone.addEventListener('dragleave', () => {
                zone.classList.remove('drag-over');
            });

            zone.addEventListener('drop', (e) => {
                e.preventDefault();
                zone.classList.remove('drag-over');

                const files = Array.from(e.dataTransfer.files);
                files.forEach(file => {
                    this.uploadFile(file);
                });
            });
        });
    }
};

// Initialize all modules when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    EduSmart.courseCatalog.init();
    EduSmart.dashboard.init();
    EduSmart.assessment.init();
    EduSmart.fileUpload.init();

    console.log('EduSmart JavaScript initialized successfully');
});

// Global utility functions
function deleteFile(fileId) {
    if (confirm('Are you sure you want to delete this file?')) {
        fetch(`/api/files/${fileId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                EduSmart.utils.showToast('File deleted successfully', 'success');
                // Remove file from list
                event.target.closest('.file-item').remove();
            } else {
                EduSmart.utils.showToast('Failed to delete file', 'error');
            }
        })
        .catch(error => {
            console.error('Delete error:', error);
            EduSmart.utils.showToast('Network error while deleting file', 'error');
        });
    }
}

// Export for module usage (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = EduSmart;
}