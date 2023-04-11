package ninjabrainbot.model.input;

import ninjabrainbot.model.IDataState;
import ninjabrainbot.model.actions.endereye.ChangeLastAngleAction;
import ninjabrainbot.model.actions.IActionExecutor;
import ninjabrainbot.model.actions.common.ResetAction;
import ninjabrainbot.model.actions.endereye.ToggleAltStdOnLastThrowAction;
import ninjabrainbot.model.actions.boat.ToggleEnteringBoatAction;
import ninjabrainbot.model.actions.common.ToggleLockedAction;
import ninjabrainbot.model.domainmodel.IDomainModel;
import ninjabrainbot.event.DisposeHandler;
import ninjabrainbot.event.IDisposable;
import ninjabrainbot.io.preferences.NinjabrainBotPreferences;

public class HotkeyInputHandler implements IDisposable {

	private final IDomainModel domainModel;
	private final IDataState dataState;
	private final IActionExecutor actionExecutor;

	private final DisposeHandler disposeHandler = new DisposeHandler();

	public HotkeyInputHandler(NinjabrainBotPreferences preferences, IDomainModel domainModel, IDataState dataState, IActionExecutor actionExecutor) {
		this.domainModel = domainModel;
		this.dataState = dataState;
		this.actionExecutor = actionExecutor;

		disposeHandler.add(preferences.hotkeyReset.whenTriggered().subscribe(this::resetIfNotLocked));
		preferences.hotkeyUndo.whenTriggered().subscribe(this::undoIfNotLocked);
		preferences.hotkeyRedo.whenTriggered().subscribe(this::redoIfNotLocked);
		preferences.hotkeyIncrement.whenTriggered().subscribe(__ -> actionExecutor.executeImmediately(new ChangeLastAngleAction(dataState, preferences, true)));
		preferences.hotkeyDecrement.whenTriggered().subscribe(__ -> actionExecutor.executeImmediately(new ChangeLastAngleAction(dataState, preferences, false)));
		preferences.hotkeyAltStd.whenTriggered().subscribe(__ -> actionExecutor.executeImmediately(new ToggleAltStdOnLastThrowAction(dataState, preferences)));
		preferences.hotkeyBoat.whenTriggered().subscribe(__ -> actionExecutor.executeImmediately(new ToggleEnteringBoatAction(dataState)));
		preferences.hotkeyLock.whenTriggered().subscribe(__ -> actionExecutor.executeImmediately(new ToggleLockedAction(dataState)));
	}

	private void resetIfNotLocked() {
		if (!dataState.locked().get())
			actionExecutor.executeImmediately(new ResetAction(domainModel));
	}

	private void undoIfNotLocked() {
		if (!dataState.locked().get())
			domainModel.undoUnderWriteLock();
	}

	private void redoIfNotLocked() {
		if (!dataState.locked().get())
			domainModel.redoUnderWriteLock();
	}

	@Override
	public void dispose() {
		disposeHandler.dispose();
	}
}
