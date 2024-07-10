import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AngularSvgIconModule } from 'angular-svg-icon';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [AngularSvgIconModule, RouterOutlet, RouterModule],
  templateUrl: './footer.component.html',
})
export class FooterComponent {
}
