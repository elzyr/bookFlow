import { CategoryInputDto } from "./CategoryInputDto";
import { Author } from "./Author";

export interface BookDto {
  /** id zwrócone przez API jako book_id */
  bookId: number;
  title: string;
  yearRelease: number;
  language: string;
  jpg: string;
  pageCount: number;
  description: string;
  /** Zwracane przez API w polu authors */
  authors: Author[];
  /** Zwracane przez API w polu categories */
  categories: CategoryInputDto[];
  totalCopies: number;
  availableCopies: number;
}