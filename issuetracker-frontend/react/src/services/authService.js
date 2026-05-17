// Заради CORS пуснах прокси при vite.config.js и затова API_BASE се промени, като го оправят в backend-a ще го променя
// const API_BASE = 'http://localhost:8080';

const API_BASE = '/api';

export const authService = {
    login: async (username, password) => {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Възникна грешка при влизане.');
        }

        return data;
    },
    
    // register: 
    // logout: 
};