import { Component, Input, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { MenuItem, User } from '../../../../../../core/models';
import { ButtonComponent } from '../../../../../../shared/components/button/button.component';
import { Store, select } from '@ngrx/store';
import {
  openDeleteMenuItemModal,
  openUpdateMenuItemModal,
} from '../../../../../../core/state/modal/menuItem/modal.actions';
import { selectCurrentUser } from '../../../../../../core/state/auth/auth.selectors';
import { Observable } from 'rxjs';

@Component({
  selector: '[menuItem-auctions-table-item]',
  templateUrl: './menuItem-auctions-table-item.component.html',
  standalone: true,
  imports: [AngularSvgIconModule, CurrencyPipe, ButtonComponent, CommonModule],
})
export class MenuItemAuctionsTableItemComponent implements OnInit {
  @Input() menuItem: MenuItem = <MenuItem>{};
  currentUser$: Observable<User | null>;

  constructor(private store: Store) {
    this.currentUser$ = this.store.pipe(select(selectCurrentUser));
  }

  ngOnInit(): void {
    console.log();
  }

  handleUpdateButtonClick(currentUser: User, menuItem: MenuItem): void {
    // Only Admin can update default items
    if (currentUser.role === 'Admin' || currentUser.role === 'Moderator' && !menuItem.default) {
      this.openUpdateModal();
    }
  }

  openUpdateModal() {
    this.store.dispatch(openUpdateMenuItemModal({ menuItem: this.menuItem }));
  }

  openDeleteModal() {
    this.store.dispatch(openDeleteMenuItemModal({ menuItemId: this.menuItem.$id }));
  }
  truncateDescription(description: string, maxLength: number = 25): string {
    if (description.length > maxLength) {
      return description.substring(0, maxLength) + '...';
    } else {
      return description;
    }
  }
}
