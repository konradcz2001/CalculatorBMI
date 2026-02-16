document.addEventListener('DOMContentLoaded', () => {
    const dateElements = document.querySelectorAll('.local-datetime');

    dateElements.forEach(el => {
        const isoDate = el.textContent.trim();
        if (isoDate) {
            const date = new Date(isoDate);
            // Format to local string (e.g. "15.05.2025, 14:30") depending on user locale
            el.textContent = date.toLocaleString(undefined, {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
            // Add full date string as a tooltip
            el.setAttribute('title', date.toString());
        }
    });
});