import { Order } from "./order.model";

export interface MenuItem {
  $id: string; // Unique identifier for the menu item.
  title: string; // Title of the menu item.
  description: string; // Description of the menu item.
  price: number; // Price of the menu item.
  imageUrl: URL; // URL to the image of the menu item.
  orders: Order[],
  category: string,
  default: boolean; //Determinate if item is created by admin or normal user
}
