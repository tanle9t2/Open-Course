document.addEventListener('DOMContentLoaded', function () {
    // Handle Set Inactive
    document.querySelectorAll('.set-inactive').forEach(button => {
        button.addEventListener('click', function () {
            const courseId = this.getAttribute('data-course-id');
            fetch(`/OpenCourse/course/${courseId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({status: "INACTIVE"})
            })
                .then(response => {
                    if (!response.ok) throw new Error("Failed to deactivate course");
                    return response.json();
                })
                .then(data => {
                    alert("Course deactivated successfully!");
                    location.reload(); // Reload to update button status
                })
                .catch(err => alert(err.message));
        });
    });

    // Handle Set Active
    document.querySelectorAll('.set-active').forEach(button => {
        button.addEventListener('click', function () {
            const courseId = this.getAttribute('data-course-id');
            fetch(`/OpenCourse/course/${courseId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                }, body: JSON.stringify({status: "ACTIVE"})
            })
                .then(response => {
                    if (!response.ok) throw new Error("Failed to activate course");
                    return response.json();
                })
                .then(data => {
                    alert("Course activated successfully!");
                    location.reload(); // Reload to update button status
                })
                .catch(err => alert(err.message));
        });
    });
})