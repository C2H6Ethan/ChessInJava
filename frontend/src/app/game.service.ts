import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  constructor(private http: HttpClient) { }

  getBoard(gameId: number): Observable<string> {
    return this.http.get<string>('/api/board', { responseType: 'text' as 'json', params: { gameId: gameId } });
  }

  getPossibleDestinationSquares(gameId: number, row: number, col: number): Observable<any> {
    return this.http.get('/api/possibleDestinationSquares', { params: { gameId: gameId, row: row, col: col} });
  }

  move(gameId: number, fromRow: number, fromCol: number, toRow: number, toCol: number): Observable<string> {
    return this.http.post<string>('api/move', {gameId: gameId, from: {row: fromRow, col: fromCol}, to: {row: toRow, col: toCol}}, { responseType: 'text' as 'json' } )
  }

  getGameState(gameId: number): Observable<{gameOver: boolean, status: string, reason: string}> {
    return this.http.get<{gameOver: boolean, status: string, reason: string}>('api/gameStatus', { params: { gameId: gameId } })
  }

  newGame(): Observable<number> {
    return this.http.post<number>('/api/newGame', null, { responseType: 'text' as 'json' });
  }

  makeRandomBotMove(gameId: number): Observable<string> {
    return this.http.post<string>('api/makeRandomBotMove', null, { responseType: 'text' as 'json', params: { gameId: gameId } })
  }

  makeGreedyBotMove(gameId: number): Observable<string> {
    return this.http.post<string>('api/makeGreedyBotMove', null, { responseType: 'text' as 'json', params: { gameId: gameId } })
  }

  makeSmartBotMove(gameId: number): Observable<string> {
    return this.http.post<string>('api/makeSmartBotMove', null, { responseType: 'text' as 'json', params: { gameId: gameId } })
  }

}
