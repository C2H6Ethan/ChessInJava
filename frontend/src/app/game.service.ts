import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  constructor(private http: HttpClient) { }

  getBoard(): Observable<string> {
    return this.http.get<string>('/api/board', { responseType: 'text' as 'json' });
  }

  getPossibleDestinationSquares(row: number, col: number): Observable<any> {
    return this.http.get('/api/possibleDestinationSquares', { params: { row: row, col: col} });
  }

  move(fromRow: number, fromCol: number, toRow: number, toCol: number): Observable<string> {
    return this.http.post<string>('api/move', {from: {row: fromRow, col: fromCol}, to: {row: toRow, col: toCol}}, { responseType: 'text' as 'json' } )
  }

}
