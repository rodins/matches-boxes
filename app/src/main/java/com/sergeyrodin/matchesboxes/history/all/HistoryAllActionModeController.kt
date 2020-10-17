package com.sergeyrodin.matchesboxes.history.all

import android.app.Activity
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.sergeyrodin.matchesboxes.R

class HistoryAllActionModeController(
    private val activity: Activity,
    private val viewModel: HistoryAllViewModel
) {
    private var actionMode: ActionMode? = null
    private val actionModeCallback = createActionModeCallback()

    private fun createActionModeCallback(): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                inflateMenu(mode, menu)
                mode.title = activity.getString(R.string.delete)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                if (item.itemId == R.id.action_delete) {
                    viewModel.deleteHighlightedPresentation()
                    return true
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                viewModel.actionModeClosed()
                actionMode = null
            }
        }
    }

    private fun inflateMenu(mode: ActionMode, menu: Menu) {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
    }

    fun startActionMode() {
        if (actionMode == null) {
            actionMode = activity.startActionMode(actionModeCallback)
        }
    }

    fun finishActionMode() {
        actionMode?.finish()
    }
}