import React, { useState, useEffect } from 'react';
import AssigneeSelect from './SidebarAssigneeSelect';
import SidebarDates from './SidebarDates';
import { ticketService } from '../../services/ticketService';
import { workflowService } from '../../services/workflowService';

const STATUS_OPTIONS = ['TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const PRIORITY_OPTIONS = ['LOWEST', 'LOW', 'MEDIUM', 'HIGH', 'HIGHEST'];

const TicketSidebar = ({ ticket, onUpdate }) => {
  const [isUpdating, setIsUpdating] = useState(false);

  const [allowedStatuses, setAllowedStatuses] = useState([ticket.ticketStatus]);

  useEffect(() => {
    const fetchAllowedTransitions = async () => {
      try {
        const rules = await workflowService.getWorkflow();
        
        // Взимаме до кои статуси можем да стигнем от текущия
        const possibleTransitions = rules[ticket.ticketStatus] || [];
        
        // В падащото меню винаги трябва да виждаме ТЕКУЩИЯ си статус + позволените нови
        setAllowedStatuses([ticket.ticketStatus, ...possibleTransitions]);
      } catch (error) {
        console.error("Грешка при зареждане на workflow", error);
        // Fallback: Ако сървърът гръмне, позволяваме всички опции, за да не блокираме работата
        setAllowedStatuses(STATUS_OPTIONS); 
      }
    };

    if (ticket && ticket.ticketStatus) {
      fetchAllowedTransitions();
    }
  }, [ticket.ticketStatus]); // Слушаме за промяна в статуса!

  const handleGeneralUpdate = async (field, value) => {
    setIsUpdating(true);
    try {
      await ticketService.updateTicket(ticket.code, { [field]: value }); 
      
      onUpdate({ ...ticket, [field]: value });
    } catch (error) {
      alert('Не успяхме да запазим промяната.');
    } finally {
      setIsUpdating(false);
    }
  };

  const dropdownStatuses = STATUS_OPTIONS.filter(status => 
    allowedStatuses.includes(status)
  );

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
          {/* Въртим цикъла върху филтрирания списък, а не върху всички! */}
          {dropdownStatuses.map(status => (
            <option key={status} value={status}>{status.replace('_', ' ')}</option>
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

      <AssigneeSelect ticket={ticket} onUpdate={onUpdate} />

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