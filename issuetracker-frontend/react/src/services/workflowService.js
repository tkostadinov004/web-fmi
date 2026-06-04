const API_BASE = '/api';

const getHeaders = () => {
    const token = localStorage.getItem('authToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};

export const workflowService = {
    getWorkflow: async (projectId) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/workflow`, {
            method: 'GET',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Грешка при зареждане на workflow.');
        }
        return response.json();
    },

    createWorkflow: async (projectId, workflowData) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/workflow`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(workflowData)
        });

        if (!response.ok) {
            throw new Error('Грешка при създаване на workflow.');
        }
        return response; 
    },

    updateWorkflow: async (projectId, workflowData) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/workflow`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(workflowData)
        });

        if (!response.ok) {
            throw new Error('Грешка при обновяване на workflow.');
        }
        return response; 
    },
    
    deleteWorkflow: async (projectId) => {
        const response = await fetch(`${API_BASE}/projects/${projectId}/workflow`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error('Грешка при изтриване на workflow.');
        }
        return response; 
    }
};