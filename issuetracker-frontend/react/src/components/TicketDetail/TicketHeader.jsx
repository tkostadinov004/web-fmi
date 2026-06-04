import React, { useState, useEffect } from 'react';
import WorkflowEditor from './WorkflowEditor';
import { projectService } from '../../services/projectService';

const TicketHeader = ({ ticketCode, projectUuid, onClose }) => {
  const [isWorkflowOpen, setIsWorkflowOpen] = useState(false);
  const [canEditWorkflow, setCanEditWorkflow] = useState(false);

  useEffect(() => {
    const fetchProjectPermissions = async () => {
      try {
        if (!projectUuid) {
          console.warn("Липсва projectUuid. Не можем да проверим правата.");
          return;
        }

        // Правим заявката с UUID на проекта
        const projectData = await projectService.getProject(projectUuid);

        // Взимаме логнатия потребител
        const myUsername = localStorage.getItem('currentUsername');

        // Проверяваме правата
        const isCreator = projectData.creator.username === myUsername;
        const myUserObj = projectData.users.find(u => u.username === myUsername);
        const hasAdminRole = myUserObj && myUserObj.roles.includes('TEAM_LEAD');

        // Отключваме бутона при съвпадение
        if (isCreator || hasAdminRole) {
          setCanEditWorkflow(true);
        } else {
          setCanEditWorkflow(false);
        }

      } catch (error) {
        console.error("Грешка при проверка на правата за workflow:", error);
        setCanEditWorkflow(false);
      }
    };

    if (projectUuid) {
      fetchProjectPermissions();
    }
  }, [projectUuid]); 

  return (
    <div className="modal-header">

      <div className="ticket-breadcrumb">
        <i className="fa-solid fa-ticket" style={{ color: 'var(--sage-green)' }}></i>
        <span>{ticketCode}</span>
      </div>

      <div className="header-actions">

        <button
          className="workflow-placeholder-btn"
          onClick={() => setIsWorkflowOpen(true)}
          title="Управление на workflow-a"
        >
          <i className="fa-solid fa-code-branch"></i> Workflow
        </button>

        <button className="icon-btn" title="Прикачи файл">
          <i className="fa-solid fa-paperclip"></i>
        </button>
        <button className="icon-btn" title="Копирай линк">
          <i className="fa-solid fa-link"></i>
        </button>

        <button className="close-btn" onClick={onClose} title="Затвори (Esc)">
          <i className="fa-solid fa-xmark"></i>
        </button>
      </div>

      {isWorkflowOpen && (
        <div className="workflow-modal-overlay">
          <div className="workflow-modal-content">
            <WorkflowEditor
              projectId={projectUuid}
              canEdit={canEditWorkflow}
              onClose={() => setIsWorkflowOpen(false)}
            />
          </div>
        </div>
      )}

    </div>
  );
};

export default TicketHeader;