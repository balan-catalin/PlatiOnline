public interface PlatiOnlineDAO {
	<E> int add(E element);
	<E> boolean update(E element);
	public boolean deleteById(int Id);
	<E> E findById(int Id);
	<E> E[] getAll(); 
}
