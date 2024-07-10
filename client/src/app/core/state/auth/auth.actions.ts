import { createAction, props } from '@ngrx/store';
import { User } from '../../models';

export const login = createAction('[Auth] Login');
export const loginSuccess = createAction('[Auth] Login Success', props<{ user: User }>());
export const loginFailure = createAction('[Auth] Login Failure', props<{ error: string }>());
export const logout = createAction('[Auth] Logout');
export const logoutSuccess = createAction('[Auth] Logout Success');
export const logoutFailure = createAction('[Auth] Logout Failure', props<{ error: string }>());
// Add a new action for session restoration success
export const restoreSessionSuccess = createAction('[Auth] Restore Session Success', props<{ user: User }>());
