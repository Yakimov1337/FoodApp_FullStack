import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgClass, NgIf } from '@angular/common';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { AuthService } from '../../../../services/auth.service';
import { Store } from '@ngrx/store';
import * as AuthActions from '../../../../core/state/auth/auth.actions';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../../environments/environment.local';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, RouterLink, AngularSvgIconModule, NgClass, NgIf, ButtonComponent],
})
export class SignInComponent implements OnInit {
  form!: FormGroup;
  submitted = false;
  passwordTextType!: boolean;
  isLoadingUser = false;
  isLoadingAdmin = false;
  isLoadingModerator = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private store: Store,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  // Admin Login
  loginAsAdmin() {
    this.isLoadingAdmin = true;
    const { email, password } = environment.adminCredentials;
    this.login(email, password, 'admin');
  }

  // Moderator Login
  loginAsModerator() {
    this.isLoadingModerator = true;
    const { email, password } = environment.moderatorCredentials;
    this.login(email, password, 'moderator');
  }

  // Normal User Login
  loginAsUser() {
    if (this.form.valid) {
      this.isLoadingUser = true;
      const { email, password } = this.form.value;
      this.login(email, password, 'user');
    } else {
      this.toastr.error('Please fill the form with proper values.');
    }
  }

  // Common login method
  private login(email: string, password: string, type: 'user' | 'admin' | 'moderator') {
    this.authService.signInAccount(email, password).subscribe({
      next: () => {
        // User is successfully authenticated, dispatch the login action
        this.store.dispatch(AuthActions.login());
        // (this as any)[`isLoading${type.charAt(0).toUpperCase() + type.slice(1)}`] = false; // makes spinner stops too early
      },
      error: (error) => {
        console.error('Error signing in:', error);
        this.toastr.error('Login failed.', error);
        (this as any)[`isLoading${type.charAt(0).toUpperCase() + type.slice(1)}`] = false; // Converts to camelCase isLoading admin or user or mod
      },
    });
  }

  get f() {
    return this.form.controls;
  }

  togglePasswordTextType() {
    this.passwordTextType = !this.passwordTextType;
  }

  googleAuth(event: Event){
    this.toastr.info('Coming soon!')
    event.preventDefault();
  }
}
