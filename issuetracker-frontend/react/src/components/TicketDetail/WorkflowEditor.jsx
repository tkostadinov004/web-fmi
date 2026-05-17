import React, { useState, useEffect } from 'react';
import { workflowService } from '../../services/WorkflowService';

const STATUS_OPTIONS = ['TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];

const WorkflowEditor = ({ canEdit, onClose }) => {
  const [workflow, setWorkflow] = useState(null);
  const [originalWorkflow, setOriginalWorkflow] = useState(null);
  
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    workflowService.getWorkflow().then((data) => {
      setWorkflow(data);
      setOriginalWorkflow(data);
      setIsLoading(false);
    });
  }, []);

  const handleCheckboxChange = (fromStatus, toStatus, isChecked) => {
    setWorkflow((prevWorkflow) => {
      const currentDestinations = prevWorkflow[fromStatus] || [];
      let newDestinations;
      
      if (isChecked) {
        newDestinations = [...currentDestinations, toStatus];
      } else {
        newDestinations = currentDestinations.filter((status) => status !== toStatus);
      }

      return { ...prevWorkflow, [fromStatus]: newDestinations };
    });
  };

  const handleSave = async () => {
    setIsSaving(true);
    await workflowService.saveWorkflow(workflow);
    setOriginalWorkflow(workflow);
    setIsSaving(false);
    setIsEditMode(false);
  };

  const handleCancelEdit = () => {
    setWorkflow(originalWorkflow);
    setIsEditMode(false);
  };

  if (isLoading) {
    return <div className="workflow-loading">Зареждане на правилата...</div>;
  }

  return (
    <div className="workflow-editor-container">
      <h3>Матрица на статусите (Workflow)</h3>
      <p className="workflow-subtitle">
        {isEditMode 
          ? "Редактирате правилата в момента. Не забравяйте да запазите." 
          : "Разгледайте към кои статуси може да преминава даден билет."}
      </p>

      <table className="workflow-matrix">
        <thead>
          <tr>
            <th>От \ Към</th>
            {STATUS_OPTIONS.map((status) => (
              <th key={status}>{status.replace('_', ' ')}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {STATUS_OPTIONS.map((rowStatus) => (
            <tr key={rowStatus}>
              <td className="row-header">
                <strong>{rowStatus.replace('_', ' ')}</strong>
              </td>
              {STATUS_OPTIONS.map((colStatus) => {
                const isAllowed = workflow[rowStatus]?.includes(colStatus);
                const isSameStatus = rowStatus === colStatus;

                return (
                  <td key={colStatus} className="checkbox-cell">
                    <input
                      type="checkbox"
                      checked={isAllowed || false}
                      // НОВО: Чекбоксът е активен САМО ако сме в Edit Mode
                      disabled={!isEditMode || isSameStatus || isSaving}
                      onChange={(e) => 
                        handleCheckboxChange(rowStatus, colStatus, e.target.checked)
                      }
                    />
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>

      <div className="workflow-actions">
        
        {!isEditMode && (
          <>
            <button 
              className="edit-workflow-btn" 
              onClick={() => setIsEditMode(true)}
              disabled={!canEdit}
              title={!canEdit ? "Нямате права да редактирате workflow-а" : "Редактирай"}
            >
              Edit Workflow
            </button>
            <button className="cancel-btn" onClick={onClose}>
              Затвори
            </button>
          </>
        )}

        {isEditMode && (
          <>
            <button 
              className="save-btn" 
              onClick={handleSave} 
              disabled={isSaving}
            >
              {isSaving ? 'Запазване...' : 'Запази промените'}
            </button>
            <button 
              className="cancel-btn" 
              onClick={handleCancelEdit} 
              disabled={isSaving}
            >
              Отказ
            </button>
          </>
        )}
        
      </div>
    </div>
  );
};

export default WorkflowEditor;