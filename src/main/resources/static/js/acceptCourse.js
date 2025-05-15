document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.btn-accept').forEach(function (button) {
        button.addEventListener('click', async function () {
            const courseId = this.getAttribute('data-id');

            if (!confirm('Are you sure you want to accept this course?')) return;

            try {
                const response = await fetch(`/OpenCourse/course/${courseId}/status`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({status: "ACTIVE"})
                });

                if (response.ok) {
                    alert('Course accepted successfully!');
                    // Optionally reload or update the UI dynamically
                    location.reload(); // or remove the row from DOM
                } else {
                    const err = await response.text();
                    alert(`Failed to accept: ${err}`);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred while accepting the course.');
            }
        });
    });
});