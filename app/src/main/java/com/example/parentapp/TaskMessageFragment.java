package com.example.parentapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;
import com.example.parentapp.models.Task;
import com.example.parentapp.models.TasksManager;

/**
 * The TaskMessageFragment class is an dialog fragment that showcases a popup message showing:
 *  Name of the task.
 *  Name and photo of child whose turn to do task.
 *  Button to confirm that child has had their turn.
 *  Cancel text to return back to the list without making any changes.
 */

public class TaskMessageFragment extends AppCompatDialogFragment {

    private TasksManager tasksManager = TasksManager.getInstance();
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private int taskIndex;
    private String taskName;
    private int currentChildIndex;
    private final int DEFAULT_NO_CHILDREN_IDX = -1;
    private View v;

    public TaskMessageFragment(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.task_view_popup_message, null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = TaskEditActivity.makeLaunchIntent(getActivity(), "Edit Current");
                intent.putExtra("editIndex", taskIndex);
                startActivity(intent);
            }
        };

        displayTaskInformation();
        attachConfirmButtonListener();
        attachAddChildMessage();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("EDIT", listener)
                .setNegativeButton("CANCEL", null)
                .create();
    }

    private void displayTaskInformation() {
        Task taskInstance = tasksManager.getTask(taskIndex);

        taskName = taskInstance.getTaskName();
        TextView txtTaskName = v.findViewById(R.id.popupTaskName);
        txtTaskName.setText(taskName);

        currentChildIndex = taskInstance.getCurrentChildIndex();
        TextView childFullName = v.findViewById(R.id.popupChildName);
        ImageView currentChildIcon = v.findViewById(R.id.popupChildImage);

        if (currentChildIndex == DEFAULT_NO_CHILDREN_IDX) {
            String noChildText = "No assigned child.";
            childFullName.setText(noChildText);
            currentChildIcon.setImageResource(R.drawable.task_no_child_icon);

        } else {
            Child childInstance = childrenManager.getChild(currentChildIndex);
            String fullName = childInstance.getFirstName() + " " + childInstance.getLastName();
            childFullName.setText(fullName);

            if (childInstance.hasPortrait()) {
                currentChildIcon.setImageBitmap(childInstance.getPortrait());
            }
        }
    }

    private void attachConfirmButtonListener() {
        Button confirmChildTurn = v.findViewById(R.id.confirmButton);

        if (currentChildIndex != DEFAULT_NO_CHILDREN_IDX) {
            confirmChildTurn.setVisibility(View.VISIBLE);
            confirmChildTurn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Update the childIndex of that task to the next child in the list.
                    if (currentChildIndex < childrenManager.getChildren().size() - 1) {
                        currentChildIndex++;
                    } else {
                        currentChildIndex = 0;
                    }

                    tasksManager.getTask(taskIndex).setCurrentChildIndex(currentChildIndex);
                    updateTasksListSharedPref();
                    dismiss();

                    WhoseTurnActivity activity = (WhoseTurnActivity) getActivity();
                    activity.onStart();
                }
            });

        } else {
            confirmChildTurn.setVisibility(View.INVISIBLE);
        }
    }

    private void attachAddChildMessage() {
        TextView addChildMessage = v.findViewById(R.id.addNewChildMessage);
        if (currentChildIndex == DEFAULT_NO_CHILDREN_IDX) {
            addChildMessage.setVisibility(View.VISIBLE);
        } else {
            addChildMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTasksListSharedPref() {
        Context context = getActivity();
        String tasksListKey = context.getResources().getString(R.string.shared_pref_tasks_list_key);
        Helpers.saveObjectToSharedPreference(context, tasksListKey, tasksManager.getTasksHistory());
    }
}
