import { createSelector } from '@ngrx/store';
import { ModalState } from './modal.reducer';

export const selectMenuItemModalState = (state: any) => state.menuItemModals;

export const selectIsCreateMenuItemModalOpen = createSelector(
  selectMenuItemModalState,
  (state: ModalState) => state.createMenuItem
);

export const selectIsUpdateMenuItemModalOpen = createSelector(
  selectMenuItemModalState,
  (state: ModalState) => state.updateMenuItem
);

export const selectIsDeleteMenuItemModalOpen = createSelector(
  selectMenuItemModalState,
  (state: ModalState) => state.deleteMenuItem
);

export const selectDeleteMenuItemId = createSelector(
  selectMenuItemModalState,
  (state: ModalState) => state.deleteMenuItemId
);

export const selectMenuItemToUpdate = createSelector(
  selectMenuItemModalState,
  (state: ModalState) => state.menuItemToUpdate
);
