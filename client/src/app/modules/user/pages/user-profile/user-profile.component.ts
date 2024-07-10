import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable, take } from 'rxjs';
import { User } from '../../../../core/models';
import { Store, select } from '@ngrx/store';
import { selectCurrentUser } from '../../../../core/state/auth/auth.selectors';
import { UserService } from '../../../../services/user.service';
import { AvatarService } from '../../../../services/avatar.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoaderComponent, ButtonComponent],
  templateUrl: './user-profile.component.html',
})
export class UserProfileComponent implements OnInit {
  currentUser: User | null = null;
  userForm: FormGroup;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  currentUserId: string | undefined;
  isUpdatingInfo: boolean = false;
  isUploadingAvatar: boolean = false;

  constructor(
    private fb: FormBuilder,
    private store: Store,
    private userService: UserService,
    private toastr: ToastrService,
    private avatarService: AvatarService,
  ) {
    this.userForm = this.fb.group({
      name: [''],
      phoneNumber: ['', [Validators.pattern(/^\+?(\d[\s-]?){1,11}\d$/)]],
      email: [{ value: '', disabled: true }],
      role: [{ value: '', disabled: true }],
    });
  }

  ngOnInit(): void {
    this.store.pipe(select(selectCurrentUser), take(1)).subscribe((user: User | null) => {
      if (user) {
        this.currentUser = user;
        this.currentUserId = user.$id;
        this.userForm.patchValue({
          name: user.name,
          email: user.email,
          phoneNumber: user.phoneNumber,
          role: user.role,
        });
        this.previewUrl = user.imageUrl?.toString() || null;
      }
    });
  }

  updatePersonalInfo(): void {
    if (this.userForm.valid && this.currentUserId) {
      const formValues = this.userForm.getRawValue();
      const hasChanges =
        this.currentUser &&
        (this.currentUser.name !== formValues.name || this.currentUser.phoneNumber !== formValues.phoneNumber);
      if (!hasChanges) {
        this.toastr.info('No changes detected. No update required.'); // STOP if no changes are made
        return;
      }
      // No more than 1 white spaces
      formValues.name = formValues.name.trim().replace(/\s+/g, ' ');
      // Reflect this change back to the form control
      this.userForm.get('name')?.setValue(formValues.name, { emitEvent: false });
      this.isUpdatingInfo = true;
      this.userService.updateUser(this.currentUserId, this.userForm.value).subscribe({
        next: () => {
          this.toastr.success('User information updated successfully');
          this.isUpdatingInfo = false;
          setTimeout(() => window.location.reload(), 100); // doing this to retrieve new user object in ngrx store (todo: create effect for user update)
        },
        error: (error) => {
          this.toastr.error('Failed to update user information');
          this.isUpdatingInfo = false;
          console.error('Failed to update user information:', error);
        },
      });
    }
  }

  handleFileInput(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    this.selectedFile = element.files ? element.files[0] : null;

    if (this.selectedFile) {
      this.previewUrl = URL.createObjectURL(this.selectedFile);
    }
  }

  saveAvatar(): void {
    if (this.selectedFile && this.currentUserId) {
      this.isUploadingAvatar = true;
      this.avatarService
        .uploadAvatar(this.selectedFile, this.currentUserId)
        .then(() => {
          this.toastr.success('Avatar updated successfully');
          this.isUploadingAvatar = false;
          setTimeout(() => window.location.reload(), 100); // doing this to retrieve new user object in ngrx store (todo: create effect for user update)
        })
        .catch((error) => {
          this.toastr.error('Error updating avatar');
          this.isUploadingAvatar = false;
          console.error('Error uploading avatar:', error);
        });
    }
  }

  useDefaultAvatar(): void {
    if (this.currentUserId && this.currentUser?.email) {
      this.isUploadingAvatar = true;
      this.avatarService
        .setDefaultAvatar(this.currentUserId, this.currentUser.email)
        .then(() => {
          this.isUploadingAvatar = false;
          setTimeout(() => window.location.reload(), 100); // doing this to retrieve new user object in ngrx store (todo: create effect for user update)
        })
        .catch((error) => console.error(error));
    }
  }
}
