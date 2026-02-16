document.addEventListener('DOMContentLoaded', () => {
    // Check if Chart.js is loaded and data exists
    if (typeof Chart === 'undefined' || typeof bmiHistoryData === 'undefined' || bmiHistoryData.length === 0) {
        return;
    }

    const ctx = document.getElementById('bmiChart');
    if (!ctx) return;

    // Data comes from Thymeleaf in DESC order (newest first), reverse for chart (time progression)
    const sortedData = [...bmiHistoryData].reverse();

    // Map data and format dates to local timezone
    const labels = sortedData.map(item => {
        const date = new Date(item.date);
        return date.toLocaleString(undefined, {
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    });

    const weightData = sortedData.map(item => item.weight);
    const bmiData = sortedData.map(item => item.bmi);

    // Helper to get current CSS variables
    const getThemeColors = () => {
        const style = getComputedStyle(document.body);
        return {
            primary: style.getPropertyValue('--primary-color').trim() || '#10b981',
            text: style.getPropertyValue('--text-primary').trim() || '#111827',
            grid: style.getPropertyValue('--border-color').trim() || '#e5e7eb'
        };
    };

    let colors = getThemeColors();

    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: chartLabels.weight,
                    data: weightData,
                    borderColor: colors.primary,
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    yAxisID: 'y',
                    tension: 0.3,
                    fill: true
                },
                {
                    label: chartLabels.bmi,
                    data: bmiData,
                    borderColor: '#3b82f6', // Blue for BMI
                    borderDash: [5, 5],
                    yAxisID: 'y1',
                    tension: 0.3,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                legend: {
                    labels: {
                        color: colors.text
                    }
                },
                tooltip: {
                    callbacks: {
                        title: function(context) {
                            return context[0].label;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: colors.grid
                    },
                    ticks: {
                        color: colors.text
                    }
                },
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    grid: {
                        color: colors.grid
                    },
                    ticks: {
                        color: colors.text
                    },
                    title: {
                        display: true,
                        text: chartLabels.weight,
                        color: colors.text
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    grid: {
                        drawOnChartArea: false,
                    },
                    ticks: {
                        color: colors.text
                    },
                    title: {
                        display: true,
                        text: chartLabels.bmi,
                        color: colors.text
                    }
                }
            }
        }
    });

    // Observer to detect theme changes (dark/light mode) on the html element
    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.attributeName === 'data-theme') {
                const newColors = getThemeColors();

                // Update chart options with new colors
                chart.options.plugins.legend.labels.color = newColors.text;

                chart.options.scales.x.grid.color = newColors.grid;
                chart.options.scales.x.ticks.color = newColors.text;

                chart.options.scales.y.grid.color = newColors.grid;
                chart.options.scales.y.ticks.color = newColors.text;
                chart.options.scales.y.title.color = newColors.text;

                chart.options.scales.y1.ticks.color = newColors.text;
                chart.options.scales.y1.title.color = newColors.text;

                // Update dataset colors if primary color changes
                chart.data.datasets[0].borderColor = newColors.primary;

                chart.update();
            }
        });
    });

    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});