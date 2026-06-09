import React, { useState, useEffect } from 'react';
import { workflowService } from '../../services/workflowService';
import WorkflowMatrix from './WorkflowMatrix';

const WorkflowEditor = ({ projectId, canEdit, onClose }) => {
  const [workflow, setWorkflow] = useState(null);
  const [originalWorkflow, setOriginalWorkflow] = useState(null);
  const [isNewWorkflow, setIsNewWorkflow] = useState(false); 
  
  const [newStatusText, setNewStatusText] = useState(''); 

  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    const fetchWorkflow = async () => {
      setIsLoading(true);
      try {
        const data = await workflowService.getWorkflow(projectId);
        setWorkflow(data);
        setOriginalWorkflow(data);
        setIsNewWorkflow(false);
      } catch (error) {
        console.warn("Проектът няма workflow, създаваме нов по подразбиране.");
        const defaultWf = {
          workflowStatuses: ['TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'],
          initialStatus: 'TO_DO',
          transitions: []
        };
        setWorkflow(defaultWf);
        setOriginalWorkflow(defaultWf);
        setIsNewWorkflow(true); 
      } finally {
        setIsLoading(false);
      }
    };

    if (projectId) {
      fetchWorkflow();
    }
  }, [projectId]);

  const handleCheckboxChange = (sourceStatus, targetStatus, isChecked) => {
    setWorkflow((prev) => {
      let updatedTransitions = [...prev.transitions];
      
      if (isChecked) {
        updatedTransitions.push({ source: sourceStatus, target: targetStatus });
      } else {
        updatedTransitions = updatedTransitions.filter(
          (t) => !(t.source === sourceStatus && t.target === targetStatus)
        );
      }

      return { ...prev, transitions: updatedTransitions };
    });
  };

  const handleAddCustomStatus = () => {
    if (!newStatusText.trim()) return;
    
    const formattedStatus = newStatusText.trim().toUpperCase().replace(/\s+/g, '_');
    
    if (!workflow.workflowStatuses.includes(formattedStatus)) {
      setWorkflow((prev) => ({
        ...prev,
        workflowStatuses: [...prev.workflowStatuses, formattedStatus]
      }));
    }
    setNewStatusText('');
  };

  const handleSave = async () => {
    setIsSaving(true);
    try {
      if (isNewWorkflow) {
        await workflowService.createWorkflow(projectId, workflow); 
        setIsNewWorkflow(false);
      } else {
        await workflowService.updateWorkflow(projectId, workflow); 
      }
      
      setOriginalWorkflow(workflow);
      setIsEditMode(false);
    } catch (error) {
      console.error(error);
      alert('Грешка при запазване на workflow.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancelEdit = () => {
    setWorkflow(originalWorkflow); 
    setIsEditMode(false);
    setNewStatusText('');
  };

  if (isLoading) {
    return <div className="workflow-loading">Зареждане на правилата...</div>;
  }

  const activeTransitions = new Set(
    workflow?.transitions.map(t => `${t.source}-${t.target}`) || []
  );

  return (
    <div className="workflow-editor-container">
      <h3>Матрица на статусите (Workflow)</h3>
      <p className="workflow-subtitle">
        {isEditMode 
          ? "Редактирате правилата в момента. Не забравяйте да запазите." 
          : "Разгледайте към кои статуси може да преминава даден билет."}
      </p>

      <div className="workflow-initial-status" style={{ marginBottom: '15px' }}>
        <label><strong>Начален статус на нов билет: </strong></label>
        <select 
          value={workflow.initialStatus} 
          onChange={(e) => setWorkflow({...workflow, initialStatus: e.target.value})}
          disabled={!isEditMode || isSaving}
        >
          {workflow.workflowStatuses.map(status => (
            <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>
          ))}
        </select>
      </div>

      <WorkflowMatrix 
        workflow={workflow}
        activeTransitions={activeTransitions}
        isEditMode={isEditMode}
        isSaving={isSaving}
        onCheckboxChange={handleCheckboxChange}
      />

      {isEditMode && (
        <div className="workflow-add-status" style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
          <input 
            type="text" 
            placeholder="Име на нов статус (напр. QA)" 
            value={newStatusText}
            onChange={(e) => setNewStatusText(e.target.value)}
            disabled={isSaving}
          />
          <button onClick={handleAddCustomStatus} disabled={isSaving || !newStatusText.trim()}>
            Добави статус
          </button>
        </div>
      )}

      <div className="workflow-actions" style={{ marginTop: '20px' }}>
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
            <button className="save-btn" onClick={handleSave} disabled={isSaving}>
              {isSaving ? 'Запазване...' : 'Запази промените'}
            </button>
            <button className="cancel-btn" onClick={handleCancelEdit} disabled={isSaving}>
              Отказ
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default WorkflowEditor;