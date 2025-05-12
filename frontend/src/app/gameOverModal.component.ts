import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'game-over-modal',
  templateUrl: './gameOverModal.component.html',
  standalone: true,
  imports: [CommonModule]
})
export class ModalComponent {
  @Input() title: string = '';
  @Input() subtext: string = '';
  @Input() show: boolean = false;
  @Output() newGame = new EventEmitter<void>

  onNewGame () {
    this.newGame.emit();
  }
}
