import { CategoryInputDto } from "./CategoryInputDto";
import { Author } from "./Author";

export interface BookDto {
  bookId: number;
  title: string;
  yearRelease: number;
  language: string;
  jpg: string;
  pageCount: number;
  description: string;
  authors: Author[];
  categories: CategoryInputDto[];
  totalCopies: number;
  availableCopies: number;
}