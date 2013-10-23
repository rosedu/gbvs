package ro.ieval.cosciclete;

final class User{
	public final String name;
	public final String group;
	public final boolean manager;

	public User(final String name, final String group, final boolean manager){
		this.name = name;
		this.group = group;
		this.manager = manager;
	}
}
