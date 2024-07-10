import { MenuItem } from "./food-menu-item.model";
import { User } from "./user.model";

export interface Order {
  $id: string;
  user: User;
  menuItems: MenuItem[];
  totalCost: number;
  createdOn: Date;
  paid: boolean,
  status: 'pending' | 'completed' | 'cancelled';
}


// New OrderSubmission model for API submission
export interface OrderSubmission {
  user: string; // User ID as a string
  menuItems: string[]; // Array of MenuItem IDs as strings
  totalCost: number;
  createdOn: string;
  paid: boolean;
  status: 'pending' | 'completed' | 'cancelled';
}
