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

export const ticketService = {
    getTicket: async (projectId, ticketCode) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/tickets/${ticketCode}`, {
            method: 'GET',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Не успяхме да заредим информацията за билета.');
        }

        return response.json();
    },

    updateTicket: async (projectId, ticketCode, data) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/tickets/${ticketCode}`, {
            method: 'PATCH',
            headers: getHeaders(),
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('Грешка при обновяване на билета.');
        }

        return response; 
    },

    getComments: async (projectId, ticketCode) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/tickets/${ticketCode}/comments?page_number=1&page_size=50`, {
            method: 'GET',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Грешка при изтегляне на коментарите');
        }

        return response.json();
    },

    addComment: async (projectId, ticketCode, content) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/tickets/${ticketCode}/comments`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({ content })
        });

        if (!response.ok) {
            throw new Error('Не успяхме да добавим коментара.');
        }

        return response;
    },
    
    updateAssignee: async (projectId, ticketCode, username) => {
        const method = username === "" ? 'DELETE' : 'PATCH';
        const url = username === "" 
            ? `${API_BASE}/projects/${projectId}/tickets/${ticketCode}/assignee`
            : `${API_BASE}/projects/${projectId}/tickets/${ticketCode}/assignee?assigneeUsername=${username}`;

        const response = await fetch(url, { 
            method, 
            headers: getHeaders() 
        });

        if (!response.ok) {
            throw new Error('Грешка при промяна на изпълнител');
        }

        return response;
    },

    updateComment: async (commentUuid, content) => {
        const response = await fetch(`${API_BASE}/comments/${commentUuid}`, {
            method: 'PATCH',
            headers: getHeaders(),
            body: JSON.stringify({ content })
        });

        if (!response.ok) {
            throw new Error('Грешка при редактиране на коментара.');
        }

        return response;
    },

    deleteComment: async (commentUuid) => {
        const response = await fetch(`${API_BASE}/comments/${commentUuid}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (!response.ok) {
            const error = new Error('Грешка при изтриване на коментара');
            error.status = response.status; 
            throw error;
        }

        return response;
    },

    getUsers: async () => {
        const response = await fetch(`${API_BASE}/users?page_number=1&page_size=100`, {
            method: 'GET',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Грешка при зареждане на потребителите');
        }

        return response.json();
    }
};