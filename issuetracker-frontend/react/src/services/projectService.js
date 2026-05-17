// Заради CORS пуснах прокси при vite.config.js и затова API_BASE се промени, като го оправят в backend-a ще го променя
// const API_BASE = 'http://localhost:8080';

const API_BASE = '/api';

const getHeaders = () => {
    const token = localStorage.getItem('authToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};

export const projectService = {
    getProject: async (projectId) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}`, {
            method: 'GET',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Грешка при зареждане на информацията за проекта.');
        }

        return response.json();
    }
};