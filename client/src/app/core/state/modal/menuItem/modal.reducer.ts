import { createReducer, on } from '@ngrx/store';
import * as ModalActions from './modal.actions';
import { MenuItem } from '../../../models';

export interface ModalState {
  createMenuItem: boolean;
  updateMenuItem: boolean;
  deleteMenuItem: boolean;
  deleteMenuItemId: string | null | undefined;
  menuItemToUpdate: MenuItem | null;
}

export const initialState: ModalState = {
  createMenuItem: false,
  updateMenuItem: false,
  deleteMenuItem: false,
  deleteMenuItemId: null,
  menuItemToUpdate: null,
};

export const menuItemsModalReducer = createReducer(
  initialState,
  on(ModalActions.openCreateMenuItemModal, state => ({ ...state, createMenuItem: true })),
  on(ModalActions.closeCreateMenuItemModal, state => ({ ...state, createMenuItem: false })),
  on(ModalActions.openUpdateMenuItemModal, (state, { menuItem }) => ({ ...state, updateMenuItem: true, menuItemToUpdate: menuItem })),
  on(ModalActions.closeUpdateMenuItemModal, state => ({ ...state, updateMenuItem: false})),
  on(ModalActions.openDeleteMenuItemModal, (state, { menuItemId }) => ({ ...state, deleteMenuItem: true, deleteMenuItemId: menuItemId })),
  on(ModalActions.closeDeleteMenuItemModal, state => ({ ...state, deleteMenuItem: false}))
);
