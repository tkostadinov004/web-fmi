import React, { useState, useEffect } from "react";
import AssigneeSelect from "./SidebarAssigneeSelect";
import SidebarDates from "./SidebarDates";
import toastr from "../../services/toastrClient";
import { ticketService } from "../../services/ticketService";
import { workflowService } from "../../services/workflowService";
import handleToastrError from "../../toastrUtils";

const FALLBACK_STATUSES = ["TO_DO", "IN_PROGRESS", "IN_REVIEW", "DONE"];
const PRIORITY_OPTIONS = ["LOWEST", "LOW", "MEDIUM", "HIGH", "HIGHEST"];

const TicketSidebar = ({ projectId, ticket, onUpdate }) => {
  const [isUpdating, setIsUpdating] = useState(false);

  const [allowedStatuses, setAllowedStatuses] = useState([ticket.ticketStatus]);

  useEffect(() => {
    const fetchAllowedTransitions = async () => {
      try {
        const workflowData = await workflowService.getWorkflow(projectId);
        console.log(workflowData);

        const uniqueStatuses = Array.from(new Set([ticket.ticketStatus, ...workflowData.workflowStatuses]));
        setAllowedStatuses(uniqueStatuses);
      } catch (error) {
        handleToastrError(error);

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
      handleToastrError(error);
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="modal-sidebar">
      <div className="sidebar-group">
        <label>Статус</label>
        <select className="sidebar-select status-select" value={ticket.ticketStatus} onChange={(e) => handleGeneralUpdate("ticketStatus", e.target.value)} disabled={isUpdating}>
          {allowedStatuses.map((status) => (
            <option key={status} value={status}>
              {status.replace(/_/g, " ")}
            </option>
          ))}
        </select>
      </div>

      <div className="sidebar-group">
        <label>Приоритет</label>
        <select className="sidebar-select" value={ticket.ticketPriority} onChange={(e) => handleGeneralUpdate("ticketPriority", e.target.value)} disabled={isUpdating}>
          {PRIORITY_OPTIONS.map((priority) => (
            <option key={priority} value={priority}>
              {priority}
            </option>
          ))}
        </select>
      </div>

      <AssigneeSelect projectId={projectId} ticket={ticket} onUpdate={onUpdate} />

      <SidebarDates
        createDate={ticket.createDate}
        updateDate={ticket.updateDate}
        dueDate={ticket.dueDate}
        onDueDateChange={(newDate) => handleGeneralUpdate("dueDate", newDate)}
        isUpdating={isUpdating}
      />
    </div>
  );
};

export default TicketSidebar;
