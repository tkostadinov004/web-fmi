// Фалшиви данни, които имитират отговора от бекенда
const MOCK_WORKFLOW = {
  "TO_DO": ["IN_PROGRESS"],
  "IN_PROGRESS": ["TO_DO", "IN_REVIEW", "DONE"],
  "IN_REVIEW": ["IN_PROGRESS", "DONE"],
  "DONE": ["IN_PROGRESS"]
};

export const workflowService = {
  getWorkflow: () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(MOCK_WORKFLOW);
      }, 500);
    });
  },

  saveWorkflow: (newWorkflowData) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log("Данните са 'запазени' в бекенда:", newWorkflowData);
        resolve({ success: true });
      }, 500);
    });
  }
};