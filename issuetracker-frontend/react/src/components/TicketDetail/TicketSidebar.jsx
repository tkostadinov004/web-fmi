import React, { useState, useEffect } from 'react';
import AssigneeSelect from './SidebarAssigneeSelect';
import SidebarDates from './SidebarDates';
import { ticketService } from '../../services/ticketService';
import { workflowService } from '../../services/workflowService';

const FALLBACK_STATUSES = ['TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const PRIORITY_OPTIONS = ['LOWEST', 'LOW', 'MEDIUM', 'HIGH', 'HIGHEST'];

const TicketSidebar = ({ projectId, ticket, onUpdate }) => {
  const [isUpdating, setIsUpdating] = useState(false);

  const [allowedStatuses, setAllowedStatuses] = useState([ticket.ticketStatus]);

  useEffect(() => {
    const fetchAllowedTransitions = async () => {
      try {
        // 1. Подаваме projectId
        const workflowData = await workflowService.getWorkflow(projectId);
        
        // 2. Филтрираме новия масив transitions, за да намерим накъде можем да отидем
        const possibleTransitions = workflowData.transitions
          .filter(t => t.source === ticket.ticketStatus)
          .map(t => t.target);
        
        // 3. Комбинираме текущия статус и позволените (ползваме Set, за да сме сигурни, че няма дубликати)
        const uniqueStatuses = Array.from(new Set([ticket.ticketStatus, ...possibleTransitions]));
        setAllowedStatuses(uniqueStatuses);
      } catch (error) {
        console.error("Грешка при зареждане на workflow", error);
        // Fallback: Ако сървърът гръмне, позволяваме базовите + текущия
        const fallbackUnique = Array.from(new Set([ticket.ticketStatus, ...FALLBACK_STATUSES]));
        setAllowedStatuses(fallbackUnique); 
      }
    };

    if (projectId && ticket && ticket.ticketStatus) {
      fetchAllowedTransitions();
    }
  }, [projectId, ticket.ticketStatus]); 

  const handleGeneralUpdate = async (field, value) => {
    setIsUpdating(true);
    try {
      await ticketService.updateTicket(projectId, ticket.code, { [field]: value }); 
      
      onUpdate({ ...ticket, [field]: value });
    } catch (error) {
      alert('Не успяхме да запазим промяната.');
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="modal-sidebar">
      
      <div className="sidebar-group">
        <label>Статус</label>
        <select 
          className="sidebar-select status-select"
          value={ticket.ticketStatus}
          onChange={(e) => handleGeneralUpdate('ticketStatus', e.target.value)}
          disabled={isUpdating}
        >
          {/* Вече въртим директно allowedStatuses, защото може да имаме къстъм статуси (напр. QA) */}
          {allowedStatuses.map(status => (
            <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>
          ))}
        </select>
      </div>

      <div className="sidebar-group">
        <label>Приоритет</label>
        <select 
          className="sidebar-select"
          value={ticket.ticketPriority}
          onChange={(e) => handleGeneralUpdate('ticketPriority', e.target.value)}
          disabled={isUpdating}
        >
          {PRIORITY_OPTIONS.map(priority => (
            <option key={priority} value={priority}>{priority}</option>
          ))}
        </select>
      </div>

      <AssigneeSelect
        projectId={projectId}
        ticket={ticket} 
        onUpdate={onUpdate} 
      />

      <SidebarDates 
        createDate={ticket.createDate}
        updateDate={ticket.updateDate}
        dueDate={ticket.dueDate}
        onDueDateChange={(newDate) => handleGeneralUpdate('dueDate', newDate)}
        isUpdating={isUpdating}
      />

    </div>
  );
};

export default TicketSidebar;