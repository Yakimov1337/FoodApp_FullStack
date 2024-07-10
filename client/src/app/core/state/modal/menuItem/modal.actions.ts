import { createAction, props } from '@ngrx/store';
import { MenuItem } from '../../../models/index';

//MenuItems related modals
export const openCreateMenuItemModal = createAction('[Modal] Open Create MenuItem Modal');
export const closeCreateMenuItemModal = createAction('[Modal] Close Create MenuItem Modal');

export const openUpdateMenuItemModal = createAction('[Modal] Open Update MenuItem Modal', props<{ menuItem: MenuItem }>());
export const closeUpdateMenuItemModal = createAction('[Modal] Close Update MenuItem Modal');

export const openDeleteMenuItemModal = createAction('[Modal] Open Delete MenuItem Modal', props<{ menuItemId: string | undefined }>());
export const closeDeleteMenuItemModal = createAction('[Modal] Close Delete MenuItem Modal');
