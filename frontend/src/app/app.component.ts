import { Component, AfterViewInit, ViewChild } from '@angular/core';
import { GameService } from './game.service';
import { CommonModule } from '@angular/common';
import { ModalComponent } from './gameOverModal.component';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [CommonModule, ModalComponent],
  standalone: true,
  styleUrl: './app.component.scss'
})
export class AppComponent implements AfterViewInit {
  @ViewChild('canvas', { static: true }) board: any;
  squareSize = 75;
  canvasSize: number = this.squareSize * 8;

  gameId = 0;
  possibleDestinationSquares: any[] = [];
  lastClickedSquare: { row: number, col: number } | null = null;

  showGameOverModal = false;
  modalTitle = '';
  modalSubtext = '';

  isRequestInProgress = false;

  // Image cache to preload images and not make them flicker when redrawing the board
  private pieceImages: Map<string, HTMLImageElement> = new Map();
  private imagesLoaded = false;

  constructor(private gameService: GameService) {}

  async ngAfterViewInit() {
    // Preload all piece images first
    await this.preloadPieceImages();
    this.imagesLoaded = true;

    // Then get initial board state
    await this.newGame();
  }

  private async preloadPieceImages(): Promise<void> {
    const pieceTypes = ['p', 'r', 'n', 'b', 'q', 'k'];
    const colors = ['w', 'b'];

    const loadPromises: Promise<void>[] = [];

    for (const color of colors) {
      for (const type of pieceTypes) {
        const pieceName = `${color}${type}`;
        const img = new Image();
        this.pieceImages.set(pieceName, img);

        const loadPromise = new Promise<void>((resolve) => {
          img.onload = () => resolve();
          img.src = "https://www.chess.com/chess-themes/pieces/neo/" + (this.squareSize <= 300 ? this.squareSize : 300) + "/" + pieceName + ".png";
        });

        loadPromises.push(loadPromise);
      }
    }

    await Promise.all(loadPromises);
  }

  drawBoard(boardFEN: string) {
    if (!this.imagesLoaded) return;

    const ctx = this.board.nativeElement.getContext('2d');

    // Clear board
    ctx.clearRect(0, 0, this.board.nativeElement.width, this.board.nativeElement.height);

    // Draw chess board squares
    for (let row = 0; row < 8; row++) {
      for (let col = 0; col < 8; col++) {
        ctx.fillStyle = (row + col) % 2 === 0 ? '#eee' : '#777';
        ctx.fillRect(col * this.squareSize, row * this.squareSize, this.squareSize, this.squareSize);
      }
    }

    // Draw pieces from FEN string
    const ranks = boardFEN.split(" ")[0].split("/");
    const whitePieces = "PRNBQK";
    let row = 0;
    let col = 0;

    for (const rank of ranks) {
      for (let i = 0; i < rank.length; i++) {
        const char = rank[i];
        const charNumber = Number(char);

        if (isNaN(charNumber)) {
          const pieceName = whitePieces.includes(char)
            ? "w" + char.toLowerCase()
            : "b" + char;

          const img = this.pieceImages.get(pieceName);
          if (img) {
            ctx.drawImage(
              img,
              col * this.squareSize,
              row * this.squareSize,
              this.squareSize,
              this.squareSize
            );
          }

          col++;
        } else {
          col += charNumber;
        }
      }
      row++;
      col = 0;
    }

    // Draw possible destination squares
    for (const square of this.possibleDestinationSquares) {
      const guiRow = 7 - square['row'];
      const guiCol = square['col'];
      const x = guiCol * this.squareSize + this.squareSize / 2;
      const y = guiRow * this.squareSize + this.squareSize / 2;
      const radius = this.squareSize / 6;

      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(180, 174, 174, 0.5)'; // Semi-transparent
      ctx.fill();
    }
  }

  async onCanvasClick(event: MouseEvent) {
    if (this.isRequestInProgress) return;
    this.isRequestInProgress = true;

    try {
      const canvas = this.board.nativeElement;
      const rect = canvas.getBoundingClientRect();

      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;

      const guiRow = Math.floor(y / this.squareSize);
      const guiCol = Math.floor(x / this.squareSize);

      const row = 7 - guiRow;
      const col = guiCol;

      if (this.possibleDestinationSquares.length === 0) {
        this.possibleDestinationSquares = await firstValueFrom(this.gameService.getPossibleDestinationSquares(this.gameId, row, col));
        this.drawBoard(await firstValueFrom(this.gameService.getBoard(this.gameId)));
      } else {
        const isPossibleDestinationSquare = this.possibleDestinationSquares.some(
          square => square['row'] === row && square['col'] === col
        );

        if (isPossibleDestinationSquare && this.lastClickedSquare) {
          let fen = await firstValueFrom(this.gameService.move(this.gameId, this.lastClickedSquare.row, this.lastClickedSquare.col, row, col))
          this.possibleDestinationSquares = [];
          this.drawBoard(fen);

          await this.checkGameState();

          if (!this.showGameOverModal) {
            let fen = await firstValueFrom(this.gameService.makeSmartBotMove(this.gameId));
            this.drawBoard(fen);
            await this.checkGameState();
          }
        } else {
          this.possibleDestinationSquares = await firstValueFrom(this.gameService.getPossibleDestinationSquares(this.gameId, row, col));
          this.drawBoard(await firstValueFrom(this.gameService.getBoard(this.gameId)));
        }
      }

      this.lastClickedSquare = { row, col };

    } finally {
      this.isRequestInProgress = false;
    }
  }


  async checkGameState() {
    let gameState= await firstValueFrom(this.gameService.getGameState(this.gameId));
    if (gameState.gameOver) {
      // game is over, show game over modal
      this.modalTitle = gameState.status;
      this.modalSubtext = gameState.reason;
      this.showGameOverModal = true;
    }
  }

  async newGame() {
    this.showGameOverModal = false;

    // Await game creation
    this.gameId = await firstValueFrom(this.gameService.newGame());

    // Await initial board fetch
    this.drawBoard(await firstValueFrom(this.gameService.getBoard(this.gameId)));


    //await this.runBotVsBot();
  }

  closeGameOverModal() {
    this.showGameOverModal = false;
  }


  private async runBotVsBot() {
    let gameOver = false;
    let counter = 0;

    while (!gameOver) {
      await this.delay(500);
      let isWhitesTurn = counter % 2 == 0;
      // make greedy bot go first, then random bot
      const boardFEN = await firstValueFrom(isWhitesTurn ? this.gameService.makeSmartBotMove(this.gameId) : this.gameService.makeGreedyBotMove(this.gameId));
      counter++;
      this.drawBoard(boardFEN);

      // Await game state check
      const gameState = await firstValueFrom(this.gameService.getGameState(this.gameId));
      if (gameState.gameOver) {
        this.modalTitle = gameState.status;
        this.modalSubtext = gameState.reason;
        this.showGameOverModal = true;
        gameOver = true;
      }
    }
  }

  private delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

}
